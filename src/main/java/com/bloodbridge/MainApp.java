package com.bloodbridge;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Главный класс приложения BloodBridge.
 * Инициализирует и запускает приложение.
 * Управляет жизненным циклом приложения и навигацией.
 */
public class MainApp extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        showLogin();
        stage.show();
    }

    public static void showLogin() {
        try {
            System.out.println("Загрузка формы входа...");
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/bloodbridge/view/Login.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Вход в систему");
            primaryStage.setScene(scene);
            System.out.println("Форма входа успешно загружена");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке формы входа: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showStaffDashboard() {
        try {
            System.out.println("Загрузка панели персонала...");
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/bloodbridge/view/StaffDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Панель персонала");
            primaryStage.setScene(scene);
            System.out.println("Панель персонала успешно загружена");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке панели персонала: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showAdminDashboard() {
        try {
            System.out.println("Загрузка панели администратора...");
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/bloodbridge/view/AdminDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Панель администратора");
            primaryStage.setScene(scene);
            System.out.println("Панель администратора успешно загружена");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке панели администратора: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showDonorProfile() {
        try {
            System.out.println("Загрузка профиля донора...");
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/bloodbridge/view/DonorProfile.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Профиль донора");
            primaryStage.setScene(scene);
            System.out.println("Профиль донора успешно загружен");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке профиля донора: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void showRegistration() {
        try {
            System.out.println("Загрузка формы регистрации...");
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/com/bloodbridge/view/Registration.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Регистрация");
            primaryStage.setScene(scene);
            System.out.println("Форма регистрации успешно загружена");
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке формы регистрации: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}