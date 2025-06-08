package com.bloodbridge.controller;

import com.bloodbridge.model.Achievement;
import com.bloodbridge.service.AchievementService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * Контроллер для выдачи достижений в системе BloodBridge.
 * Управляет процессом награждения доноров.
 * Обрабатывает выбор и выдачу достижений.
 */
public class AwardAchievementController {
    @FXML
    private ComboBox<Achievement> achievementComboBox;
    
    @FXML
    private Label descriptionLabel;
    
    private AchievementService achievementService;
    private String donorId;
    
    public void initialize() {
        achievementService = new AchievementService();
        
        // Настраиваем отображение достижений в комбобоксе
        achievementComboBox.setCellFactory(new Callback<ListView<Achievement>, ListCell<Achievement>>() {
            @Override
            public ListCell<Achievement> call(ListView<Achievement> param) {
                return new ListCell<Achievement>() {
                    @Override
                    protected void updateItem(Achievement item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getIcon() + " " + item.getTitle());
                        }
                    }
                };
            }
        });
        
        // Добавляем слушатель для отображения описания
        achievementComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                descriptionLabel.setText(newVal.getDescription());
            } else {
                descriptionLabel.setText("");
            }
        });
        
        // Загружаем список доступных достижений
        achievementComboBox.getItems().addAll(achievementService.getAllAchievements());
        
        // Устанавливаем отображение выбранного значения
        achievementComboBox.setButtonCell(new ListCell<Achievement>() {
            @Override
            protected void updateItem(Achievement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIcon() + " " + item.getTitle());
                }
            }
        });
    }
    
    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }
    
    public Achievement getSelectedAchievement() {
        return achievementComboBox.getValue();
    }
} 