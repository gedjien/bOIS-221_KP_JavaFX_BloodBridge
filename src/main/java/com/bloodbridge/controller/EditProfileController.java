package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.service.DonorService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Контроллер для редактирования профиля пользователя в системе BloodBridge.
 * Управляет процессом изменения профиля.
 * Обрабатывает обновление данных пользователя.
 */
public class EditProfileController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> bloodGroupComboBox;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextArea medicalHistoryArea;

    @FXML
    public void initialize() {
        bloodGroupComboBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        
        Donor currentDonor = DonorService.getCurrentDonor();
        if (currentDonor != null) {
            nameField.setText(currentDonor.getFullName());
            emailField.setText(currentDonor.getEmail());
            phoneField.setText(currentDonor.getPhone());
            bloodGroupComboBox.setValue(currentDonor.getBloodGroup());
            medicalHistoryArea.setText(currentDonor.getMedicalHistory());
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }

        Donor currentDonor = DonorService.getCurrentDonor();
        if (currentDonor != null) {
            currentDonor.setFullName(nameField.getText());
            currentDonor.setEmail(emailField.getText());
            currentDonor.setPhone(phoneField.getText());
            currentDonor.setBloodGroup(bloodGroupComboBox.getValue());

            if (!newPasswordField.getText().isEmpty()) {
                currentDonor.setPassword(newPasswordField.getText());
            }

            DonorService.updateDonor(currentDonor);
            showSuccess("Успех", "Профиль успешно обновлен");
            closeWindow();
        }
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() ||
            phoneField.getText().isEmpty() || bloodGroupComboBox.getValue() == null) {
            showError("Ошибка", "Пожалуйста, заполните все обязательные поля");
            return false;
        }

        if (!newPasswordField.getText().isEmpty()) {
            if (!currentPasswordField.getText().equals(DonorService.getCurrentDonor().getPassword())) {
                showError("Ошибка", "Неверный текущий пароль");
                return false;
            }
            if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                showError("Ошибка", "Новые пароли не совпадают");
                return false;
            }
        }

        return true;
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
} 