package com.bloodbridge.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import com.bloodbridge.model.Donor;
import com.bloodbridge.service.DonorService;

/**
 * Контроллер для регистрации новых доноров в системе BloodBridge.
 * Управляет процессом регистрации.
 * Обрабатывает ввод данных нового донора.
 */
public class DonorRegistrationController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<String> bloodGroupComboBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextArea medicalHistoryArea;
    @FXML
    private Button registerButton;
    @FXML
    private Label errorLabel;

    private final DonorService donorService = new DonorService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bloodGroupComboBox.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    }

    @FXML
    private void handleRegister() {
        if (!validateInput()) {
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String bloodGroup = bloodGroupComboBox.getValue();
        String password = passwordField.getText();
        String medicalHistory = medicalHistoryArea.getText().trim();

        try {
            Donor donor = new Donor();
            donor.setId(System.currentTimeMillis());
            donor.setFullName(name);
            donor.setEmail(email);
            donor.setPhone(phone);
            donor.setBloodGroup(bloodGroup);
            donor.setPassword(password);
            donor.setMedicalHistory(medicalHistory);
            donor.setMedicalNotes("");
            donor.setStatus("Активен");

            DonorService.addDonor(donor);
            showSuccess("Успех", "Регистрация успешно завершена");
            openLoginWindow();
        } catch (Exception e) {
            showError("Ошибка", "Не удалось зарегистрировать донора: " + e.getMessage());
        }
    }

    private void openLoginWindow() {
        try {
            URL resourceUrl = getClass().getResource("/com/bloodbridge/view/Login.fxml");
            if (resourceUrl == null) {
                showError("Ошибка", "Не удалось загрузить форму входа");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Вход в систему");
            stage.setScene(new Scene(root));
            
            // Закрываем текущее окно
            Stage currentStage = (Stage) nameField.getScene().getWindow();
            currentStage.close();
            
            stage.show();
        } catch (IOException e) {
            showError("Ошибка", "Не удалось открыть форму входа");
        }
    }

    @FXML
    private void handleBack() {
        openLoginWindow();
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || 
            phoneField.getText().isEmpty() || bloodGroupComboBox.getValue() == null ||
            passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            showError("Ошибка", "Пожалуйста, заполните все обязательные поля");
            return false;
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showError("Ошибка", "Пароли не совпадают");
            return false;
        }

        // Проверка формата email
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Ошибка", "Неверный формат email");
            return false;
        }

        // Проверка формата телефона
        if (!phoneField.getText().matches("^\\+?[0-9]{10,15}$")) {
            showError("Ошибка", "Неверный формат телефона");
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
}