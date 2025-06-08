package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;

/**
 * Контроллер для редактирования данных донора в системе BloodBridge.
 * Управляет процессом изменения данных.
 * Обрабатывает обновление информации о доноре.
 */
public class EditDonorController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> bloodGroupCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea medicalHistoryArea;
    @FXML private TextArea medicalNotesArea;
    @FXML private Label errorLabel;

    private Donor donor;

    @FXML
    public void initialize() {
        bloodGroupCombo.getItems().addAll("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        statusCombo.getItems().addAll("Активный", "Временный", "Постоянный", "Отстранен");
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
        if (donor != null) {
            nameField.setText(donor.getFullName());
            emailField.setText(donor.getEmail());
            phoneField.setText(donor.getPhone());
            bloodGroupCombo.setValue(donor.getBloodGroup());
            statusCombo.setValue(donor.getStatus());
            medicalHistoryArea.setText(donor.getMedicalHistory());
            medicalNotesArea.setText(donor.getMedicalNotes());
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            donor.setFullName(nameField.getText());
            donor.setEmail(emailField.getText());
            donor.setPhone(phoneField.getText());
            donor.setBloodGroup(bloodGroupCombo.getValue());
            donor.setStatus(statusCombo.getValue());
            donor.setMedicalHistory(medicalHistoryArea.getText());
            donor.setMedicalNotes(medicalNotesArea.getText());
            // Диалог закроется автоматически по OK
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите ФИО донора");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showError("Введите email донора");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            showError("Введите телефон донора");
            return false;
        }
        if (bloodGroupCombo.getValue() == null) {
            showError("Выберите группу крови");
            return false;
        }
        if (statusCombo.getValue() == null) {
            showError("Выберите статус донора");
            return false;
        }
        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
} 