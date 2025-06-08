package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Donation;
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
 * Контроллер для добавления новой донации в системе BloodBridge.
 * Управляет процессом регистрации донации.
 * Обрабатывает ввод данных о донации.
 */
public class AddDonationController implements Initializable {
    @FXML private Label donorNameLabel;
    @FXML private Label donorBloodGroupLabel;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField bloodVolumeField;
    @FXML private TextArea notesArea;
    @FXML private Label errorLabel;
    
    private Donor donor;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Добавляем временные слоты с 9:00 до 18:00 с интервалом в 30 минут
        for (int hour = 9; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                String time = String.format("%02d:%02d", hour, minute);
                timeComboBox.getItems().add(time);
            }
        }
        
        // Устанавливаем текущую дату
        datePicker.setValue(LocalDate.now());
        
        // Устанавливаем ближайшее доступное время
        updateAvailableTimes();
        
        // Устанавливаем значение по умолчанию для объема крови
        bloodVolumeField.setText("450");
        
        // Добавляем слушатель изменения даты
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateAvailableTimes();
        });
    }
    
    private void updateAvailableTimes() {
        LocalDate selectedDate = datePicker.getValue();
        LocalTime currentTime = LocalTime.now();
        LocalDate currentDate = LocalDate.now();
        
        timeComboBox.getItems().clear();
        
        for (int hour = 9; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                LocalTime slotTime = LocalTime.of(hour, minute);
                
                // Если выбранная дата - сегодня, пропускаем прошедшее время
                if (selectedDate.equals(currentDate) && slotTime.isBefore(currentTime)) {
                    continue;
                }
                
                String time = String.format("%02d:%02d", hour, minute);
                timeComboBox.getItems().add(time);
            }
        }
        
        // Устанавливаем первое доступное время
        if (!timeComboBox.getItems().isEmpty()) {
            timeComboBox.setValue(timeComboBox.getItems().get(0));
        }
    }
    
    public void setDonor(Donor donor) {
        this.donor = donor;
        if (donor != null) {
            donorNameLabel.setText(donor.getFullName());
            donorBloodGroupLabel.setText(donor.getBloodGroup());
        }
    }
    
    @FXML
    private void handleSave() {
        try {
            if (!validateInput()) {
                return;
            }
            
            LocalDate date = datePicker.getValue();
            LocalTime time = LocalTime.parse(timeComboBox.getValue(), TIME_FORMATTER);
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            
            // Проверяем, не прошло ли выбранное время
            if (dateTime.isBefore(LocalDateTime.now())) {
                showError("Ошибка", "Нельзя записаться на прошедшее время");
                return;
            }
            
            int bloodVolume = Integer.parseInt(bloodVolumeField.getText());
            
            Donation donation = new Donation();
            donation.setDonor(donor);
            donation.setDateTime(dateTime);
            donation.setStatus(DonationService.STATUS_SCHEDULED);
            donation.setBloodVolume(bloodVolume);
            donation.setNotes(notesArea.getText());
            
            DonationService.addDonation(donation);
            showSuccess("Успех", "Запись на донацию успешно создана");
            closeWindow();
        } catch (Exception e) {
            showError("Ошибка", "Не удалось создать запись на донацию: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean validateInput() {
        if (datePicker.getValue() == null) {
            showError("Ошибка", "Пожалуйста, выберите дату");
            return false;
        }
        
        if (timeComboBox.getValue() == null) {
            showError("Ошибка", "Пожалуйста, выберите время");
            return false;
        }
        
        if (bloodVolumeField.getText().isEmpty()) {
            showError("Ошибка", "Пожалуйста, укажите объем крови");
            return false;
        }
        
        try {
            int volume = Integer.parseInt(bloodVolumeField.getText());
            if (volume < 200 || volume > 500) {
                showError("Ошибка", "Объем крови должен быть от 200 до 500 мл");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Ошибка", "Объем крови должен быть числом");
            return false;
        }
        
        return true;
    }
    
    private void showError(String title, String content) {
        errorLabel.setText(content);
        errorLabel.setStyle("-fx-text-fill: red;");
    }
    
    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) datePicker.getScene().getWindow();
        stage.close();
    }
} 