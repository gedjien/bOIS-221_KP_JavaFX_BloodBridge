package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Donation;
import com.bloodbridge.service.DonorService;
import com.bloodbridge.service.DonationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Контроллер для управления записями на донацию в системе BloodBridge.
 * Управляет процессом бронирования времени.
 * Обрабатывает выбор даты и времени донации.
 */
public class DonationBookingController implements Initializable {
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private Label errorLabel;
    @FXML private Label donorNameLabel;
    @FXML private Label donorBloodGroupLabel;
    
    private Donor currentDonor;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentDonor = DonorService.getCurrentDonor();
        if (currentDonor == null) {
            showError("Ошибка: пользователь не авторизован");
            return;
        }
        setupDateTimePicker();
        loadDonorData();
    }

    private void setupDateTimePicker() {
        // Устанавливаем минимальную дату на завтра
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now().plusDays(1)));
            }
        });

        // Заполняем список доступных времен
        for (int hour = 9; hour <= 17; hour++) {
            timeComboBox.getItems().add(String.format("%02d:00", hour));
        }

        // Обновляем список времен при выборе даты
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateTimeSlots(newVal);
            }
        });
    }

    private void loadDonorData() {
        if (currentDonor != null) {
            donorNameLabel.setText(currentDonor.getFullName());
            donorBloodGroupLabel.setText(currentDonor.getBloodGroup());
        }
    }

    private void updateTimeSlots(LocalDate date) {
        timeComboBox.getItems().clear();
        for (int hour = 9; hour <= 17; hour++) {
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(hour, 0));
            if (!isTimeSlotBooked(dateTime)) {
                timeComboBox.getItems().add(dateTime.format(timeFormatter));
            }
        }
    }

    private boolean isTimeSlotBooked(LocalDateTime dateTime) {
        return DonationService.getUpcomingDonations(currentDonor).stream()
                .anyMatch(donation -> donation.getDateTime().equals(dateTime));
    }

    @FXML
    private void handleBook() {
        if (datePicker.getValue() == null || timeComboBox.getValue() == null) {
            showError("Пожалуйста, выберите дату и время");
            return;
        }

        LocalDateTime dateTime = LocalDateTime.of(
            datePicker.getValue(),
            LocalTime.parse(timeComboBox.getValue(), timeFormatter)
        );

        if (isTimeSlotBooked(dateTime)) {
            showError("Выбранное время уже занято");
            return;
        }

        Donation donation = new Donation();
        donation.setDonor(currentDonor);
        donation.setDateTime(dateTime);
        donation.setStatus(DonationService.STATUS_SCHEDULED);

        DonationService.addDonation(donation);
        showSuccess("Запись успешно создана");
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
} 