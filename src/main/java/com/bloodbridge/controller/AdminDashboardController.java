package com.bloodbridge.controller;

import com.bloodbridge.model.Staff;
import com.bloodbridge.service.StaffService;
import com.bloodbridge.service.DonorService;
import com.bloodbridge.service.DonationService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Контроллер главной панели администратора в системе BloodBridge.
 * Управляет отображением административной панели.
 * Обеспечивает доступ к функциям администратора.
 */
public class AdminDashboardController {
    @FXML private TableView<Staff> staffTable;
    @FXML private TableColumn<Staff, String> nameColumn;
    @FXML private TableColumn<Staff, String> emailColumn;
    @FXML private TableColumn<Staff, String> roleColumn;
    
    @FXML private Label totalDonorsLabel;
    @FXML private Label totalStaffLabel;
    @FXML private Label totalDonationsLabel;
    
    private ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Настройка таблицы персонала
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Загрузка данных
        loadStaffData();
        updateStatistics();
    }
    
    private void loadStaffData() {
        staffList.clear();
        staffList.addAll(StaffService.getAllStaff());
        staffTable.setItems(staffList);
    }
    
    private void updateStatistics() {
        totalDonorsLabel.setText(String.valueOf(DonorService.getAllDonors().size()));
        totalStaffLabel.setText(String.valueOf(StaffService.getAllStaff().size()));
        totalDonationsLabel.setText(String.valueOf(DonationService.getAllDonations().size()));
    }
    
    @FXML
    private void addStaff() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com.bloodbridge.view/StaffRegistration.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Добавление сотрудника");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadStaffData();
        } catch (IOException e) {
            showError("Ошибка при открытии формы добавления сотрудника", e.getMessage());
        }
    }
    
    @FXML
    private void deleteStaff() {
        Staff selectedStaff = staffTable.getSelectionModel().getSelectedItem();
        if (selectedStaff != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление сотрудника");
            alert.setContentText("Вы уверены, что хотите удалить сотрудника " + selectedStaff.getFullName() + "?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                StaffService.deleteStaff(selectedStaff.getId());
                loadStaffData();
                updateStatistics();
            }
        } else {
            showError("Ошибка", "Пожалуйста, выберите сотрудника для удаления");
        }
    }
    
    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com.bloodbridge.view/Login.fxml"));
            Stage stage = (Stage) staffTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Ошибка при выходе", e.getMessage());
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