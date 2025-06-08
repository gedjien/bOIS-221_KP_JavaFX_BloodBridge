package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.service.DonorService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер для управления медицинскими заметками в системе BloodBridge.
 * Управляет процессом создания и редактирования медицинских записей.
 * Обеспечивает доступ к истории медицинских наблюдений.
 */
public class MedicalNotesController {
    @FXML private Label donorNameLabel;
    @FXML private Label donorBloodGroupLabel;
    @FXML private DatePicker datePicker;
    @FXML private TextField systolicPressure;
    @FXML private TextField diastolicPressure;
    @FXML private TextField hemoglobinField;
    @FXML private TextField weightField;
    @FXML private CheckBox pregnancyCheck;
    @FXML private CheckBox menstruationCheck;
    @FXML private CheckBox hivCheck;
    @FXML private CheckBox hepatitisCheck;
    @FXML private CheckBox malariaCheck;
    @FXML private CheckBox tuberculosisCheck;
    @FXML private CheckBox cancerCheck;
    @FXML private CheckBox diabetesCheck;
    @FXML private CheckBox heartDiseaseCheck;
    @FXML private CheckBox bloodDiseaseCheck;
    @FXML private TextArea notesArea;
    @FXML private Label errorLabel;
    
    private Donor donor;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void setDonor(Donor donor) {
        this.donor = donor;
        if (donor != null) {
            donorNameLabel.setText(donor.getFullName());
            donorBloodGroupLabel.setText(donor.getBloodGroup());
            datePicker.setValue(LocalDate.now());
            loadExistingNotes();
        }
    }

    private void loadExistingNotes() {
        if (donor != null && donor.getMedicalNotes() != null && !donor.getMedicalNotes().isEmpty()) {
            String[] notes = donor.getMedicalNotes().split("\n");
            for (String note : notes) {
                if (note.startsWith("Дата:")) {
                    try {
                        String dateStr = note.substring(6).trim();
                        datePicker.setValue(LocalDate.parse(dateStr, DATE_FORMATTER));
                    } catch (Exception e) {
                        datePicker.setValue(LocalDate.now());
                    }
                } else if (note.startsWith("Давление:")) {
                    String[] pressure = note.substring(9).trim().split("/");
                    if (pressure.length == 2) {
                        systolicPressure.setText(pressure[0].trim());
                        diastolicPressure.setText(pressure[1].trim());
                    }
                } else if (note.startsWith("Гемоглобин:")) {
                    hemoglobinField.setText(note.substring(11).trim());
                } else if (note.startsWith("Вес:")) {
                    weightField.setText(note.substring(4).trim());
                } else if (note.startsWith("Противопоказания:")) {
                    String contraindications = note.substring(18).trim();
                    pregnancyCheck.setSelected(contraindications.contains("Беременность"));
                    menstruationCheck.setSelected(contraindications.contains("Менструация"));
                    hivCheck.setSelected(contraindications.contains("ВИЧ"));
                    hepatitisCheck.setSelected(contraindications.contains("Гепатит"));
                    malariaCheck.setSelected(contraindications.contains("Малярия"));
                    tuberculosisCheck.setSelected(contraindications.contains("Туберкулез"));
                    cancerCheck.setSelected(contraindications.contains("Онкологические заболевания"));
                    diabetesCheck.setSelected(contraindications.contains("Сахарный диабет"));
                    heartDiseaseCheck.setSelected(contraindications.contains("Заболевания сердца"));
                    bloodDiseaseCheck.setSelected(contraindications.contains("Заболевания крови"));
                } else if (note.startsWith("Заметки:")) {
                    notesArea.setText(note.substring(8).trim());
                }
            }
        }
    }

    @FXML
    private void handleSave() {
        if (donor != null) {
            StringBuilder notes = new StringBuilder();
            
            // Основная информация
            notes.append("Дата: ").append(datePicker.getValue().format(DATE_FORMATTER)).append("\n");
            notes.append("Давление: ").append(systolicPressure.getText()).append("/").append(diastolicPressure.getText()).append("\n");
            notes.append("Гемоглобин: ").append(hemoglobinField.getText()).append("\n");
            notes.append("Вес: ").append(weightField.getText()).append("\n");
            
            // Противопоказания
            List<String> contraindications = new ArrayList<>();
            if (pregnancyCheck.isSelected()) contraindications.add("Беременность");
            if (menstruationCheck.isSelected()) contraindications.add("Менструация");
            if (hivCheck.isSelected()) contraindications.add("ВИЧ");
            if (hepatitisCheck.isSelected()) contraindications.add("Гепатит");
            if (malariaCheck.isSelected()) contraindications.add("Малярия");
            if (tuberculosisCheck.isSelected()) contraindications.add("Туберкулез");
            if (cancerCheck.isSelected()) contraindications.add("Онкологические заболевания");
            if (diabetesCheck.isSelected()) contraindications.add("Сахарный диабет");
            if (heartDiseaseCheck.isSelected()) contraindications.add("Заболевания сердца");
            if (bloodDiseaseCheck.isSelected()) contraindications.add("Заболевания крови");
            
            notes.append("Противопоказания: ").append(String.join(", ", contraindications)).append("\n");
            
            // Дополнительные заметки
            if (!notesArea.getText().isEmpty()) {
                notes.append("Заметки: ").append(notesArea.getText());
            }
            
            donor.setMedicalNotes(notes.toString());
            DonorService.updateDonor(donor);
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) notesArea.getScene().getWindow();
        stage.close();
    }
} 