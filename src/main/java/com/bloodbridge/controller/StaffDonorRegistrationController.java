package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.service.DonorService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Контроллер для регистрации доноров персоналом в системе BloodBridge.
 * Управляет процессом регистрации новых доноров.
 * Обрабатывает ввод и валидацию данных донора.
 */
public class StaffDonorRegistrationController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> bloodGroupComboBox;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextArea medicalHistoryArea;
    @FXML private Label errorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBloodGroupComboBox();
    }

    private void setupBloodGroupComboBox() {
        bloodGroupComboBox.getItems().addAll(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        );
    }

    @FXML
    private void handleRegister() {
        if (!validateInput()) {
            return;
        }

        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String bloodGroup = bloodGroupComboBox.getValue();
        String password = passwordField.getText();

        String error = DonorService.validateDonorData(name, email, phone, bloodGroup);
        if (error != null) {
            showError("Ошибка валидации", error);
            return;
        }

        Donor donor = new Donor();
        donor.setFullName(name);
        donor.setEmail(email);
        donor.setPhone(phone);
        donor.setBloodGroup(bloodGroup);
        donor.setPassword(password);

        DonorService.addDonor(donor);
        showSuccess("Регистрация успешна", "Донор успешно зарегистрирован в системе.");
        closeWindow();
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() ||
            phoneField.getText().isEmpty() || bloodGroupComboBox.getValue() == null ||
            passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            showError("Ошибка", "Пожалуйста, заполните все поля");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Ошибка", "Пароли не совпадают");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
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