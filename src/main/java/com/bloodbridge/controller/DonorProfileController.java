package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Donation;
import com.bloodbridge.model.Achievement;
import com.bloodbridge.service.DonorService;
import com.bloodbridge.service.DonationService;
import com.bloodbridge.service.AchievementService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления профилем донора в системе BloodBridge.
 * Управляет отображением и редактированием профиля.
 * Обрабатывает изменения данных донора.
 */
public class DonorProfileController implements Initializable {
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label bloodGroupLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalDonationsLabel;
    @FXML private Label lastDonationLabel;
    @FXML private Label nextDonationLabel;
    
    @FXML private TableView<Donation> donationsTable;
    @FXML private TableColumn<Donation, String> dateColumn;
    @FXML private TableColumn<Donation, String> statusColumn;
    @FXML private TableColumn<Donation, Integer> bloodVolumeColumn;
    @FXML private TableColumn<Donation, String> notesColumn;
    
    @FXML private TableView<Achievement> achievementsTable;
    @FXML private TableColumn<Achievement, String> achievementNameColumn;
    @FXML private TableColumn<Achievement, String> achievementDescriptionColumn;
    @FXML private TableColumn<Achievement, String> achievementDateColumn;
    
    @FXML private TextArea medicalNotesArea;
    
    private Donor donor;
    private final ObservableList<Donation> donations = FXCollections.observableArrayList();
    private final ObservableList<Achievement> achievements = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final AchievementService achievementService = new AchievementService();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Инициализация DonorProfileController...");
        try {
            donor = DonorService.getCurrentDonor();
            System.out.println("Текущий донор: " + (donor != null ? donor.getFullName() : "null"));
            
            setupProfile();
            setupTables();
            loadData();
            
            System.out.println("Инициализация завершена успешно");
        } catch (Exception e) {
            System.err.println("Ошибка при инициализации: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupProfile() {
        try {
            if (donor != null) {
                nameLabel.setText(donor.getFullName());
                emailLabel.setText(donor.getEmail());
                phoneLabel.setText(donor.getPhone());
                bloodGroupLabel.setText(donor.getBloodGroup());
                statusLabel.setText("Активен");
                
                // Устанавливаем медицинскую информацию
                if (medicalNotesArea != null) {
                    StringBuilder medicalInfo = new StringBuilder();
                    
                    // Медицинская история
                    if (donor.getMedicalHistory() != null && !donor.getMedicalHistory().isEmpty()) {
                        medicalInfo.append("Медицинская история:\n");
                        medicalInfo.append(donor.getMedicalHistory()).append("\n\n");
                    } else {
                        medicalInfo.append("Медицинская история:\n");
                        medicalInfo.append("Аллергия: Нет\n");
                        medicalInfo.append("Хронические заболевания: Нет\n");
                        medicalInfo.append("Операции: Нет\n");
                        medicalInfo.append("Принимаемые лекарства: Нет\n\n");
                    }
                    
                    // Последняя оценка донора
                    if (donor.getMedicalNotes() != null && !donor.getMedicalNotes().isEmpty()) {
                        medicalInfo.append("Последняя оценка:\n");
                        medicalInfo.append(donor.getMedicalNotes());
                    }
                    
                    medicalNotesArea.setText(medicalInfo.toString());
                }
            }
            System.out.println("Профиль настроен успешно");
        } catch (Exception e) {
            System.err.println("Ошибка при настройке профиля: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupTables() {
        System.out.println("Настройка таблиц...");
        try {
            // Настройка таблицы донаций
            dateColumn.setCellValueFactory(cellData -> {
                LocalDateTime dateTime = cellData.getValue().getDateTime();
                return new javafx.beans.property.SimpleStringProperty(
                    dateTime != null ? dateTime.format(DATE_FORMATTER) : ""
                );
            });
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            bloodVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("bloodVolume"));
            notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
            
            // Настройка таблицы достижений
            achievementNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            achievementDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            achievementDateColumn.setCellValueFactory(cellData -> {
                LocalDateTime dateTime = cellData.getValue().getDateAwarded();
                return new javafx.beans.property.SimpleStringProperty(
                    dateTime != null ? dateTime.format(DATE_FORMATTER) : ""
                );
            });
            
            System.out.println("Таблицы настроены успешно");
        } catch (Exception e) {
            System.err.println("Ошибка при настройке таблиц: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadData() {
        System.out.println("Загрузка данных...");
        try {
            if (donor != null) {
                List<Donation> donations = DonationService.getDonationsByDonorId(donor.getId());
                System.out.println("Найдено донаций: " + donations.size());
                
                // Общее количество донаций
                if (totalDonationsLabel != null) {
                    int completedDonations = (int) donations.stream()
                        .filter(d -> DonationService.STATUS_COMPLETED.equals(d.getStatus()))
                        .count();
                    System.out.println("Завершенных донаций: " + completedDonations);
                    totalDonationsLabel.setText("Всего донаций: " + completedDonations);
                }
                
                // Загружаем данные в таблицы
                if (donationsTable != null) {
                    donationsTable.setItems(FXCollections.observableArrayList(donations));
                }
                
                // Загружаем достижения
                if (achievementsTable != null) {
                    List<Achievement> achievements = achievementService.getDonorAchievements(donor.getId().toString());
                    achievementsTable.setItems(FXCollections.observableArrayList(achievements));
                }
                
                System.out.println("Данные загружены успешно");
            }
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBookDonation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/AddDonation.fxml"));
            Parent root = loader.load();
            
            AddDonationController controller = loader.getController();
            controller.setDonor(donor);
            
            Stage stage = new Stage();
            stage.setTitle("Запись на донацию");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            loadData();
        } catch (IOException e) {
            System.err.println("Ошибка при открытии формы записи на донацию: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка", "Не удалось открыть форму записи на донацию");
        }
    }
    
    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/DonorDetails.fxml"));
            Parent root = loader.load();
            
            DonorDetailsController controller = loader.getController();
            controller.setDonor(donor);
            
            Stage stage = new Stage();
            stage.setTitle("Редактирование профиля");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            setupProfile();
        } catch (IOException e) {
            System.err.println("Ошибка при открытии формы редактирования профиля: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка", "Не удалось открыть форму редактирования профиля");
        }
    }
    
    @FXML
    private void handleLogout() {
        DonorService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Ошибка при выходе из системы: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка", "Не удалось выйти из системы");
        }
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 