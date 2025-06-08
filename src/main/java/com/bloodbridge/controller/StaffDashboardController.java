package com.bloodbridge.controller;

import com.bloodbridge.model.Donation;
import com.bloodbridge.model.Donor;
import com.bloodbridge.service.DonationService;
import com.bloodbridge.service.DonorService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер главной панели персонала в системе BloodBridge.
 * Управляет отображением рабочей панели сотрудников.
 * Обеспечивает доступ к основным функциям системы.
 */
public class StaffDashboardController {
    @FXML private TableView<Donation> donationsTable;
    @FXML private TableColumn<Donation, String> donorColumn;
    @FXML private TableColumn<Donation, String> dateColumn;
    @FXML private TableColumn<Donation, String> statusColumn;
    @FXML private TableColumn<Donation, String> bloodVolumeColumn;
    @FXML private TableColumn<Donation, String> notesColumn;

    @FXML private TableView<Donor> donorsTable;
    @FXML private TableColumn<Donor, String> nameColumn;
    @FXML private TableColumn<Donor, String> emailColumn;
    @FXML private TableColumn<Donor, String> phoneColumn;
    @FXML private TableColumn<Donor, String> bloodGroupColumn;
    @FXML private TableColumn<Donor, String> donorStatusColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private TextField donorSearchField;
    @FXML private TextField donationSearchField;

    @FXML private Label errorLabel;

    private ObservableList<Donor> donors;
    private FilteredList<Donor> filteredDonors;
    private ObservableList<Donation> donations;
    private FilteredList<Donation> filteredDonations;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private DonationService donationService;
    private DonorService donorService;

    @FXML
    public void initialize() {
        System.out.println("Инициализация панели персонала...");
        
        donationService = new DonationService();
        donorService = new DonorService();
        
        setupDonationsTable();
        setupDonorsTable();
        loadData();
        
        // Добавляем слушатели для поиска
        donorSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDonors(newValue);
        });
        
        donationSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDonations(newValue);
        });
    }

    private void setupDonationsTable() {
        System.out.println("Настройка таблицы донаций...");
        try {
            donorColumn.setCellValueFactory(cellData -> {
                Donor donor = cellData.getValue().getDonor();
                return new SimpleStringProperty(donor != null ? donor.getFullName() : "");
            });
            
            dateColumn.setCellValueFactory(cellData -> {
                LocalDateTime dateTime = cellData.getValue().getDateTime();
                String formattedDate = dateTime != null ? 
                    dateTime.format(DATE_FORMATTER) : "";
                return new SimpleStringProperty(formattedDate);
            });
            
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            bloodVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("bloodVolume"));
            notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
            System.out.println("Таблица донаций успешно настроена");
        } catch (Exception e) {
            System.err.println("Ошибка при настройке таблицы донаций: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupDonorsTable() {
        System.out.println("Настройка таблицы доноров...");
        try {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            bloodGroupColumn.setCellValueFactory(new PropertyValueFactory<>("bloodGroup"));
            donorStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

            // Добавляем обработчик двойного клика
            donorsTable.setRowFactory(tv -> {
                TableRow<Donor> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        Donor donor = row.getItem();
                        // Добавляем класс для анимации
                        row.getStyleClass().add("pressed");
                        
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/DonorDetails.fxml"));
                            Parent root = loader.load();
                            
                            DonorDetailsController controller = loader.getController();
                            controller.setDonor(donor);
                            
                            Stage stage = new Stage();
                            stage.setTitle("Детали донора");
                            stage.setScene(new Scene(root));
                            stage.initModality(Modality.APPLICATION_MODAL);
                            stage.show();
                            
                            // Удаляем класс анимации после открытия окна
                            row.getStyleClass().remove("pressed");
                        } catch (IOException e) {
                            e.printStackTrace();
                            showError("Не удалось открыть детали донора: " + e.getMessage());
                            row.getStyleClass().remove("pressed");
                        }
                    }
                });
                return row;
            });
            
            System.out.println("Таблица доноров успешно настроена");
        } catch (Exception e) {
            System.err.println("Ошибка при настройке таблицы доноров: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupFilters() {
        System.out.println("Настройка фильтров...");
        try {
            statusFilter.getItems().addAll(
                "Все",
                DonationService.STATUS_SCHEDULED,
                DonationService.STATUS_COMPLETED,
                DonationService.STATUS_CANCELLED
            );
            statusFilter.setValue("Все");
            
            // Добавляем обработчик изменения значения фильтра
            statusFilter.setOnAction(e -> {
                String selectedStatus = statusFilter.getValue();
                filteredDonations.setPredicate(donation -> {
                    if ("Все".equals(selectedStatus)) {
                        return true;
                    }
                    return selectedStatus.equals(donation.getStatus());
                });
            });
            
            System.out.println("Фильтры успешно настроены");
        } catch (Exception e) {
            System.err.println("Ошибка при настройке фильтров: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadData() {
        System.out.println("Загрузка данных...");
        try {
            donors = FXCollections.observableArrayList(DonorService.getAllDonors());
            filteredDonors = new FilteredList<>(donors);
            donorsTable.setItems(filteredDonors);

            donations = FXCollections.observableArrayList(DonationService.getAllDonations());
            filteredDonations = new FilteredList<>(donations);
            donationsTable.setItems(filteredDonations);
            System.out.println("Данные успешно загружены");
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddDonor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/DonorRegistration.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Добавить донора");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        } catch (IOException e) {
            showError("Ошибка", "Не удалось открыть окно добавления донора");
        }
    }

    @FXML
    private void handleDeleteDonor() {
        Donor selectedDonor = donorsTable.getSelectionModel().getSelectedItem();
        if (selectedDonor != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText(null);
            alert.setContentText("Вы уверены, что хотите удалить этого донора?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                DonorService.removeDonor(selectedDonor);
                donors.remove(selectedDonor);
            }
        } else {
            showError("Ошибка", "Пожалуйста, выберите донора для удаления");
        }
    }

    @FXML
    private void handleAddNote() {
        Donor selectedDonor = donorsTable.getSelectionModel().getSelectedItem();
        if (selectedDonor != null) {
            TextInputDialog dialog = new TextInputDialog(selectedDonor.getMedicalNotes());
            dialog.setTitle("Добавить заметку");
            dialog.setHeaderText(null);
            dialog.setContentText("Введите заметку о доноре:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(note -> {
                selectedDonor.setMedicalNotes(note);
                DonorService.updateDonor(selectedDonor);
                loadData();
            });
        } else {
            showError("Ошибка", "Пожалуйста, выберите донора для добавления заметки");
        }
    }

    @FXML
    private void handleAddDonation() {
        Donor selectedDonor = donorsTable.getSelectionModel().getSelectedItem();
        if (selectedDonor == null) {
            showError("Ошибка", "Пожалуйста, выберите донора из списка");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/AddDonation.fxml"));
            Parent root = loader.load();
            
            AddDonationController controller = loader.getController();
            controller.setDonor(selectedDonor);
            
            Stage stage = new Stage();
            stage.setTitle("Добавление донации");
            stage.setScene(new Scene(root));
            
            stage.showAndWait();
            loadData(); // Перезагружаем данные после закрытия окна
        } catch (IOException e) {
            System.err.println("Ошибка при открытии формы добавления донации: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка", "Не удалось открыть форму добавления донации");
        }
    }

    @FXML
    private void handleCancelDonation() {
        updateDonationStatus(DonationService.STATUS_CANCELLED);
    }

    @FXML
    private void handleCompleteDonation() {
        updateDonationStatus(DonationService.STATUS_COMPLETED);
    }

    @FXML
    private void handleMissedDonation() {
        updateDonationStatus("Пропущена");
    }

    @FXML
    private void handleScheduledDonation() {
        updateDonationStatus(DonationService.STATUS_SCHEDULED);
    }

    @FXML
    private void handleDeleteDonation() {
        Donation selectedDonation = donationsTable.getSelectionModel().getSelectedItem();
        if (selectedDonation != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение");
            alert.setHeaderText(null);
            alert.setContentText("Вы уверены, что хотите удалить эту донацию?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                DonationService.removeDonation(selectedDonation);
                donations.remove(selectedDonation);
            }
        } else {
            showError("Ошибка", "Пожалуйста, выберите донацию для удаления");
        }
    }

    @FXML
    private void handleMedicalNotes() {
        Donor selectedDonor = donorsTable.getSelectionModel().getSelectedItem();
        if (selectedDonor != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/MedicalNotes.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Медицинские заметки");
                stage.setScene(new Scene(root));
                
                MedicalNotesController controller = loader.getController();
                controller.setDonor(selectedDonor);
                
                stage.showAndWait();
                loadData(); // Перезагружаем данные после закрытия окна
            } catch (IOException e) {
                showError("Ошибка", "Не удалось открыть форму медицинских заметок");
            }
        } else {
            showError("Ошибка", "Пожалуйста, выберите донора для просмотра медицинских заметок");
        }
    }

    @FXML
    private void handleAssessDonor() {
        Donor selectedDonor = donorsTable.getSelectionModel().getSelectedItem();
        if (selectedDonor != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Оценка донора");
            dialog.setHeaderText(null);

            ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Добавляем поля для медицинской истории
            TextField allergiesField = new TextField();
            allergiesField.setPromptText("Например: Нет или список аллергий");
            
            TextField chronicDiseasesField = new TextField();
            chronicDiseasesField.setPromptText("Например: Нет или список заболеваний");
            
            TextField surgeriesField = new TextField();
            surgeriesField.setPromptText("Например: Нет или список операций");
            
            TextField medicationsField = new TextField();
            medicationsField.setPromptText("Например: Нет или список лекарств");

            // Загружаем существующие данные
            if (selectedDonor.getMedicalHistory() != null && !selectedDonor.getMedicalHistory().isEmpty()) {
                String[] lines = selectedDonor.getMedicalHistory().split("\n");
                for (String line : lines) {
                    if (line.startsWith("Аллергия:")) {
                        allergiesField.setText(line.substring(10).trim());
                    } else if (line.startsWith("Хронические заболевания:")) {
                        chronicDiseasesField.setText(line.substring(24).trim());
                    } else if (line.startsWith("Операции:")) {
                        surgeriesField.setText(line.substring(10).trim());
                    } else if (line.startsWith("Принимаемые лекарства:")) {
                        medicationsField.setText(line.substring(22).trim());
                    }
                }
            }

            grid.add(new Label("Аллергия:"), 0, 0);
            grid.add(allergiesField, 1, 0);
            grid.add(new Label("Хронические заболевания:"), 0, 1);
            grid.add(chronicDiseasesField, 1, 1);
            grid.add(new Label("Операции:"), 0, 2);
            grid.add(surgeriesField, 1, 2);
            grid.add(new Label("Принимаемые лекарства:"), 0, 3);
            grid.add(medicationsField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == saveButtonType) {
                StringBuilder medicalHistory = new StringBuilder();
                medicalHistory.append("Аллергия: ").append(allergiesField.getText().isEmpty() ? "Нет" : allergiesField.getText()).append("\n");
                medicalHistory.append("Хронические заболевания: ").append(chronicDiseasesField.getText().isEmpty() ? "Нет" : chronicDiseasesField.getText()).append("\n");
                medicalHistory.append("Операции: ").append(surgeriesField.getText().isEmpty() ? "Нет" : surgeriesField.getText()).append("\n");
                medicalHistory.append("Принимаемые лекарства: ").append(medicationsField.getText().isEmpty() ? "Нет" : medicationsField.getText());
                
                selectedDonor.setMedicalHistory(medicalHistory.toString());
                DonorService.updateDonor(selectedDonor);
                loadData();
            }
        } else {
            showError("Ошибка", "Пожалуйста, выберите донора для оценки");
        }
    }

    @FXML
    private void handleDonorAssessment() {
        Donor selectedDonor = donorsTable.getSelectionModel().getSelectedItem();
        if (selectedDonor != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/DonorAssessment.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setTitle("Оценка донора");
                stage.setScene(new Scene(root));
                
                DonorAssessmentController controller = loader.getController();
                controller.setDonor(selectedDonor);
                controller.setStage(stage);
                
                stage.showAndWait();
                loadData(); // Перезагружаем данные после закрытия окна оценки
            } catch (IOException e) {
                e.printStackTrace();
                showError("Ошибка", "Не удалось открыть форму оценки донора: " + e.getMessage());
            }
        } else {
            showError("Ошибка", "Пожалуйста, выберите донора для оценки");
        }
    }

    private void updateDonationStatus(String newStatus) {
        System.out.println("Обновление статуса донации на: " + newStatus);
        Donation selectedDonation = donationsTable.getSelectionModel().getSelectedItem();
        if (selectedDonation != null) {
            System.out.println("Выбрана донация: " + selectedDonation.getId());
            selectedDonation.setStatus(newStatus);
            try {
                DonationService.updateDonation(selectedDonation);
                System.out.println("Статус успешно обновлен");
                
                // Обновляем данные в таблице
                int selectedIndex = donationsTable.getSelectionModel().getSelectedIndex();
                donations.set(selectedIndex, selectedDonation);
                donationsTable.refresh();
                
                // Перезагружаем все данные
                loadData();
            } catch (Exception e) {
                System.err.println("Ошибка при обновлении статуса: " + e.getMessage());
                e.printStackTrace();
                showError("Ошибка", "Не удалось обновить статус: " + e.getMessage());
            }
        } else {
            System.out.println("Донация не выбрана");
            showError("Ошибка", "Пожалуйста, выберите донацию для изменения статуса");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) donationsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Ошибка", "Не удалось выполнить выход");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void filterDonors(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            donorsTable.setItems(donors);
            return;
        }

        String searchLower = searchText.toLowerCase();
        ObservableList<Donor> filteredList = FXCollections.observableArrayList();

        for (Donor donor : donors) {
            // Поиск по группе крови
            if (donor.getBloodGroup().toLowerCase().contains(searchLower)) {
                filteredList.add(donor);
                continue;
            }

            // Поиск по ФИО
            String fullName = donor.getFullName().toLowerCase();
            String[] nameParts = fullName.split("\\s+");
            
            // Проверяем, содержит ли поисковый запрос хотя бы часть имени
            boolean matches = false;
            for (String part : nameParts) {
                if (part.startsWith(searchLower) || searchLower.startsWith(part)) {
                    matches = true;
                    break;
                }
            }
            
            // Проверяем, содержит ли имя поисковый запрос
            if (fullName.contains(searchLower)) {
                matches = true;
            }

            // Поиск по email
            if (donor.getEmail().toLowerCase().contains(searchLower)) {
                matches = true;
            }

            // Поиск по телефону
            if (donor.getPhone().toLowerCase().contains(searchLower)) {
                matches = true;
            }

            if (matches) {
                filteredList.add(donor);
            }
        }

        donorsTable.setItems(filteredList);
    }

    private void filterDonations(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            donationsTable.setItems(donations);
            return;
        }

        String searchLower = searchText.toLowerCase();
        ObservableList<Donation> filteredList = FXCollections.observableArrayList();

        for (Donation donation : donations) {
            // Поиск по имени донора
            if (donation.getDonor().getFullName().toLowerCase().contains(searchLower)) {
                filteredList.add(donation);
                continue;
            }

            // Поиск по группе крови
            if (donation.getDonor().getBloodGroup().toLowerCase().contains(searchLower)) {
                filteredList.add(donation);
                continue;
            }

            // Поиск по статусу
            if (donation.getStatus().toLowerCase().contains(searchLower)) {
                filteredList.add(donation);
                continue;
            }

            // Поиск по дате
            if (donation.getDateTime() != null) {
                String formattedDate = donation.getDateTime().format(DATE_FORMATTER);
                if (formattedDate.toLowerCase().contains(searchLower)) {
                    filteredList.add(donation);
                    continue;
                }
            }

            // Поиск по объему крови
            if (String.valueOf(donation.getBloodVolume()).contains(searchText)) {
                filteredList.add(donation);
                continue;
            }

            // Поиск по заметкам
            if (donation.getNotes() != null && 
                donation.getNotes().toLowerCase().contains(searchLower)) {
                filteredList.add(donation);
                continue;
            }

            // Поиск по email донора
            if (donation.getDonor().getEmail().toLowerCase().contains(searchLower)) {
                filteredList.add(donation);
                continue;
            }

            // Поиск по телефону донора
            if (donation.getDonor().getPhone().toLowerCase().contains(searchLower)) {
                filteredList.add(donation);
            }
        }

        donationsTable.setItems(filteredList);
    }
} 