package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.service.DonorService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * Контроллер для оценки состояния донора в системе BloodBridge.
 * Управляет процессом медицинской оценки.
 * Обрабатывает ввод медицинских показателей.
 */
public class DonorAssessmentController {
    @FXML private Label donorNameLabel;
    @FXML private Label bloodGroupLabel;
    @FXML private DatePicker assessmentDatePicker;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField systolicField;
    @FXML private TextField diastolicField;
    @FXML private TextField pulseField;
    @FXML private TextField temperatureField;
    @FXML private TextField weightField;
    @FXML private TextField heightField;
    @FXML private TextField hemoglobinField;
    @FXML private CheckBox hasFever;
    @FXML private CheckBox hasCold;
    @FXML private CheckBox hasAllergies;
    @FXML private CheckBox hasMedications;
    @FXML private CheckBox hasRecentSurgery;
    @FXML private CheckBox hasPregnancy;
    @FXML private CheckBox hasMenstruation;
    @FXML private CheckBox hasTattoo;
    @FXML private CheckBox hasPiercing;
    @FXML private CheckBox hasChronicDisease;
    @FXML private CheckBox hasInfectiousDisease;
    @FXML private CheckBox hasBloodDisease;
    @FXML private CheckBox hasCardiovascularDisease;
    @FXML private CheckBox hasDiabetes;
    @FXML private CheckBox hasCancer;
    @FXML private CheckBox hasHIV;
    @FXML private CheckBox hasHepatitis;
    @FXML private CheckBox hasSyphilis;
    @FXML private CheckBox hasTuberculosis;
    @FXML private TextArea medicalNotesArea;
    @FXML private Label errorLabel;
    
    private Donor donor;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
    private Stage stage;
    
    @FXML
    public void initialize() {
        // Заполняем ComboBox для времени
        for (int hour = 8; hour <= 20; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                timeComboBox.getItems().add(String.format("%02d:%02d", hour, minute));
            }
        }
        
        // Устанавливаем текущую дату
        assessmentDatePicker.setValue(LocalDateTime.now().toLocalDate());
        
        // Устанавливаем текущее время
        int currentHour = LocalDateTime.now().getHour();
        if (currentHour >= 8 && currentHour <= 20) {
            timeComboBox.setValue(String.format("%02d:00", currentHour));
        } else {
            timeComboBox.setValue("09:00");
        }
    }
    
    public void setDonor(Donor donor) {
        this.donor = donor;
        if (donor != null) {
            donorNameLabel.setText(donor.getFullName());
            bloodGroupLabel.setText(donor.getBloodGroup());
        }
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
        if (stage != null) {
            stage.setMinWidth(600);
            stage.setMinHeight(400);
            stage.setWidth(600);
            stage.setHeight(600);
        }
    }
    
    @FXML
    private void handleSave() {
        if (!validateInput()) {
            return;
        }
        
        try {
            if (donor == null) {
                showError("Ошибка: информация о доноре не найдена");
                return;
            }
            
            // Получаем выбранное время
            String timeStr = timeComboBox.getValue();
            if (timeStr == null) {
                showError("Ошибка: время не выбрано");
                return;
            }
            LocalTime time = LocalTime.parse(timeStr);
            
            // Создаем дату и время
            if (assessmentDatePicker.getValue() == null) {
                showError("Ошибка: дата не выбрана");
                return;
            }
            LocalDateTime assessmentDateTime = assessmentDatePicker.getValue().atTime(time);
            
            // Формируем медицинские заметки
            StringBuilder notes = new StringBuilder();
            
            // Добавляем основные показатели, если они заполнены
            if (!systolicField.getText().isEmpty() && !diastolicField.getText().isEmpty()) {
                notes.append("АД: ").append(systolicField.getText()).append("/")
                     .append(diastolicField.getText()).append(" мм рт.ст.\n");
            }
            
            if (!pulseField.getText().isEmpty()) {
                notes.append("Пульс: ").append(pulseField.getText()).append(" уд/мин\n");
            }
            
            if (!temperatureField.getText().isEmpty()) {
                notes.append("Температура: ").append(temperatureField.getText()).append(" °C\n");
            }
            
            if (!weightField.getText().isEmpty()) {
                notes.append("Вес: ").append(weightField.getText()).append(" кг\n");
            }
            
            if (!heightField.getText().isEmpty()) {
                notes.append("Рост: ").append(heightField.getText()).append(" см\n");
            }
            
            if (!hemoglobinField.getText().isEmpty()) {
                notes.append("Гемоглобин: ").append(hemoglobinField.getText()).append(" г/л\n");
            }
            
            // Добавляем дополнительные показания
            boolean hasAnyCondition = hasFever.isSelected() || hasCold.isSelected() || 
                hasAllergies.isSelected() || hasMedications.isSelected() || 
                hasRecentSurgery.isSelected() || hasPregnancy.isSelected() || 
                hasMenstruation.isSelected() || hasTattoo.isSelected() || 
                hasPiercing.isSelected() || hasChronicDisease.isSelected() || 
                hasInfectiousDisease.isSelected() || hasBloodDisease.isSelected() || 
                hasCardiovascularDisease.isSelected() || hasDiabetes.isSelected() || 
                hasCancer.isSelected() || hasHIV.isSelected() || 
                hasHepatitis.isSelected() || hasSyphilis.isSelected() || 
                hasTuberculosis.isSelected();
            
            if (hasAnyCondition) {
                notes.append("\nДополнительные показания:\n");
                if (hasFever.isSelected()) notes.append("- Повышенная температура\n");
                if (hasCold.isSelected()) notes.append("- Простуда\n");
                if (hasAllergies.isSelected()) notes.append("- Аллергия\n");
                if (hasMedications.isSelected()) notes.append("- Прием лекарств\n");
                if (hasRecentSurgery.isSelected()) notes.append("- Недавняя операция\n");
                if (hasPregnancy.isSelected()) notes.append("- Беременность\n");
                if (hasMenstruation.isSelected()) notes.append("- Менструация\n");
                if (hasTattoo.isSelected()) notes.append("- Татуировка\n");
                if (hasPiercing.isSelected()) notes.append("- Пирсинг\n");
                if (hasChronicDisease.isSelected()) notes.append("- Хроническое заболевание\n");
                if (hasInfectiousDisease.isSelected()) notes.append("- Инфекционное заболевание\n");
                if (hasBloodDisease.isSelected()) notes.append("- Заболевание крови\n");
                if (hasCardiovascularDisease.isSelected()) notes.append("- Сердечно-сосудистое заболевание\n");
                if (hasDiabetes.isSelected()) notes.append("- Сахарный диабет\n");
                if (hasCancer.isSelected()) notes.append("- Онкологическое заболевание\n");
                if (hasHIV.isSelected()) notes.append("- ВИЧ\n");
                if (hasHepatitis.isSelected()) notes.append("- Гепатит\n");
                if (hasSyphilis.isSelected()) notes.append("- Сифилис\n");
                if (hasTuberculosis.isSelected()) notes.append("- Туберкулез\n");
            }
            
            // Добавляем пользовательские заметки
            if (!medicalNotesArea.getText().isEmpty()) {
                notes.append("\nДополнительные заметки:\n").append(medicalNotesArea.getText());
            }
            
            // Проверяем, есть ли хотя бы какие-то данные для сохранения
            if (notes.length() == 0) {
                showError("Пожалуйста, заполните хотя бы одно поле");
                return;
            }
            
            // Обновляем данные донора
            donor.setMedicalNotes(notes.toString());
            donor.setLastAssessmentDate(assessmentDateTime);
            
            // Сохраняем изменения
            DonorService.updateDonor(donor);
            
            closeWindow();
        } catch (Exception e) {
            showError("Ошибка при сохранении оценки: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    private boolean validateInput() {
        if (assessmentDatePicker.getValue() == null) {
            showError("Пожалуйста, выберите дату");
            return false;
        }
        
        if (timeComboBox.getValue() == null) {
            showError("Пожалуйста, выберите время");
            return false;
        }
        
        // Проверяем числовые поля, если они заполнены
        if (!systolicField.getText().isEmpty() && !NUMBER_PATTERN.matcher(systolicField.getText()).matches()) {
            showError("Систолическое давление должно быть числом");
            return false;
        }
        
        if (!diastolicField.getText().isEmpty() && !NUMBER_PATTERN.matcher(diastolicField.getText()).matches()) {
            showError("Диастолическое давление должно быть числом");
            return false;
        }
        
        if (!pulseField.getText().isEmpty() && !NUMBER_PATTERN.matcher(pulseField.getText()).matches()) {
            showError("Пульс должен быть числом");
            return false;
        }
        
        if (!temperatureField.getText().isEmpty() && !NUMBER_PATTERN.matcher(temperatureField.getText()).matches()) {
            showError("Температура должна быть числом");
            return false;
        }
        
        if (!weightField.getText().isEmpty() && !NUMBER_PATTERN.matcher(weightField.getText()).matches()) {
            showError("Вес должен быть числом");
            return false;
        }
        
        if (!heightField.getText().isEmpty() && !NUMBER_PATTERN.matcher(heightField.getText()).matches()) {
            showError("Рост должен быть числом");
            return false;
        }
        
        if (!hemoglobinField.getText().isEmpty() && !NUMBER_PATTERN.matcher(hemoglobinField.getText()).matches()) {
            showError("Гемоглобин должен быть числом");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void closeWindow() {
        stage.close();
    }
} 