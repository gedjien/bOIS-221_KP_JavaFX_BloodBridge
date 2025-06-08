package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Staff;
import com.bloodbridge.service.DonorService;
import com.bloodbridge.service.StaffService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

/**
 * Контроллер для авторизации пользователей в системе BloodBridge.
 * Управляет процессом входа в систему.
 * Обрабатывает проверку учетных данных.
 */
public class LoginController implements Initializable {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    @FXML private ComboBox<String> userTypeComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            userTypeComboBox.getItems().addAll("Донор", "Персонал");
            userTypeComboBox.setValue("Донор");
        } catch (Exception e) {
            showError("Ошибка инициализации", "Не удалось загрузить форму входа");
        }
    }

    @FXML
    private void handleLogin() {
        try {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String userType = userTypeComboBox.getValue();

            if (email.isEmpty() || password.isEmpty()) {
                showError("Ошибка", "Пожалуйста, заполните все поля");
                return;
            }

            if ("Донор".equals(userType)) {
                Optional<Donor> donor = DonorService.authenticateDonor(email, password);
                if (donor.isPresent()) {
                    showSuccess("Успех", "Вход выполнен успешно");
                    goToDonorProfile();
                } else {
                    showError("Ошибка", "Неверный email или пароль");
                }
            } else if ("Персонал".equals(userType)) {
                Staff staff = StaffService.authenticate(email, password);
                if (staff != null) {
                    StaffService.setCurrentStaff(staff);
                    showSuccess("Успех", "Вход выполнен успешно");
                    goToStaffDashboard();
                } else {
                    showError("Ошибка", "Неверный email или пароль");
                }
            }
        } catch (Exception e) {
            showError("Ошибка", "Произошла ошибка при входе в систему");
        }
    }

    @FXML
    private void handleRegister() {
        try {
            URL resourceUrl = getClass().getResource("/com/bloodbridge/view/DonorRegistration.fxml");
            if (resourceUrl == null) {
                showError("Ошибка", "Не удалось загрузить форму регистрации");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Регистрация донора");
            stage.setScene(new Scene(root));
            
            // Закрываем текущее окно
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();
            
            stage.show();
        } catch (IOException e) {
            showError("Ошибка", "Не удалось открыть форму регистрации");
        }
    }

    private void goToDonorProfile() {
        try {
            URL resourceUrl = getClass().getResource("/com/bloodbridge/view/DonorProfile.fxml");
            if (resourceUrl == null) {
                showError("Ошибка", "Не удалось загрузить профиль донора");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Ошибка", "Не удалось открыть профиль донора");
        }
    }

    private void goToStaffDashboard() {
        try {
            System.out.println("Попытка загрузки панели персонала...");
            URL resourceUrl = getClass().getResource("/com/bloodbridge/view/StaffDashboard.fxml");
            if (resourceUrl == null) {
                System.err.println("Не удалось найти файл StaffDashboard.fxml");
                showError("Ошибка", "Не удалось загрузить панель персонала");
                return;
            }
            System.out.println("Файл StaffDashboard.fxml найден: " + resourceUrl);

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            System.out.println("FXML файл успешно загружен");
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            System.out.println("Сцена успешно установлена");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке панели персонала: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка", "Не удалось открыть панель персонала");
        }
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