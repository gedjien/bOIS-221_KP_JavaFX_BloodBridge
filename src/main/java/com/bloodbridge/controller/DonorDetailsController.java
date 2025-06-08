package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Achievement;
import com.bloodbridge.service.AchievementService;
import com.bloodbridge.service.DonorService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.io.File;
import java.io.FileWriter;
import java.awt.Desktop;

/**
 * Контроллер для отображения детальной информации о доноре в системе BloodBridge.
 * Управляет отображением данных донора.
 * Обеспечивает доступ к истории донаций и достижениям.
 */
public class DonorDetailsController implements Initializable {
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label phoneLabel;
    @FXML private Label bloodGroupLabel;
    @FXML private Label statusLabel;
    @FXML private Label lastAssessmentLabel;
    @FXML private TextArea medicalHistoryArea;
    @FXML private TextArea medicalNotesArea;
    @FXML private ListView<String> achievementsList;
    @FXML private Label errorLabel;
    @FXML private Button closeButton;
    @FXML private Button printButton;

    private Donor donor;
    private DonorService donorService;
    private AchievementService achievementService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        achievementService = new AchievementService();
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
        updateUI();
    }

    public void setDonorService(DonorService donorService) {
        this.donorService = donorService;
    }

    private void updateUI() {
        if (donor != null) {
            nameLabel.setText(donor.getFullName());
            emailLabel.setText(donor.getEmail());
            phoneLabel.setText(donor.getPhone());
            bloodGroupLabel.setText(donor.getBloodGroup());
            statusLabel.setText(donor.getStatus());
            medicalHistoryArea.setText(donor.getMedicalHistory());
            medicalNotesArea.setText(donor.getMedicalNotes());
            if (donor.getLastAssessmentDate() != null) {
                lastAssessmentLabel.setText(donor.getLastAssessmentDate().format(DATE_FORMATTER));
            } else {
                lastAssessmentLabel.setText("Нет данных");
            }
            
            List<Achievement> achievements = achievementService.getDonorAchievements(donor.getId().toString());
            achievementsList.getItems().clear();
            for (Achievement achievement : achievements) {
                String achievementText = String.format("%s %s (%s)",
                    achievement.getIcon(),
                    achievement.getTitle(),
                    achievement.getDateAwarded() != null ? 
                        achievement.getDateAwarded().format(DATE_FORMATTER) : "Не указана");
                achievementsList.getItems().add(achievementText);
            }
        }
    }

    @FXML
    private void handleAwardAchievement() {
        try {
            System.out.println("Открытие диалога выдачи достижения для донора: " + donor.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/AwardAchievementDialog.fxml"));
            DialogPane dialogPane = loader.load();
            
            AwardAchievementController controller = loader.getController();
            controller.setDonorId(donor.getId().toString());
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Выдать достижение");
            
            Optional<ButtonType> result = dialog.showAndWait();
            System.out.println("Результат диалога: " + (result.isPresent() ? result.get().getText() : "Отменено"));
            
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                Achievement selectedAchievement = controller.getSelectedAchievement();
                System.out.println("Выбранное достижение: " + (selectedAchievement != null ? selectedAchievement.getTitle() : "null"));
                
                if (selectedAchievement != null) {
                    achievementService.awardAchievement(donor.getId().toString(), selectedAchievement.getId());
                    updateUI();
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при открытии диалога: " + e.getMessage());
            e.printStackTrace();
            showError("Ошибка при открытии диалога выдачи достижения");
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bloodbridge/view/EditDonorDialog.fxml"));
            DialogPane dialogPane = loader.load();
            
            EditDonorController controller = loader.getController();
            controller.setDonor(donor);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Редактировать донора");
            
            Optional<ButtonType> result = dialog.showAndWait();
            
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                updateUI();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии диалога редактирования");
        }
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление донора");
        alert.setContentText("Вы уверены, что хотите удалить донора " + donor.getFullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: Добавить логику удаления донора
            handleClose();
        }
    }

    @FXML
    private void handlePrint() {
        try {
            // Создаем временный HTML файл
            File tempFile = File.createTempFile("donor_details_", ".html");
            
            // Создаем HTML документ
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write("<!DOCTYPE html>\n");
                writer.write("<html>\n");
                writer.write("<head>\n");
                writer.write("<meta charset=\"UTF-8\">\n");
                writer.write("<title>Информация о доноре</title>\n");
                writer.write("<style>\n");
                writer.write("body { font-family: Arial, sans-serif; margin: 20px; }\n");
                writer.write("h1 { color: #2c3e50; text-align: center; }\n");
                writer.write("h2 { color: #34495e; margin-top: 20px; }\n");
                writer.write(".info { margin: 10px 0; }\n");
                writer.write(".label { font-weight: bold; }\n");
                writer.write(".achievements { list-style-type: none; padding: 0; }\n");
                writer.write(".achievements li { margin: 5px 0; }\n");
                writer.write("@media print {\n");
                writer.write("  body { margin: 0; padding: 20px; }\n");
                writer.write("  @page { margin: 2cm; }\n");
                writer.write("}\n");
                writer.write("</style>\n");
                writer.write("</head>\n");
                writer.write("<body>\n");
                
                // Заголовок
                writer.write("<h1>Информация о доноре</h1>\n");
                
                // Основная информация
                writer.write("<div class=\"info\">\n");
                writer.write("<p><span class=\"label\">ФИО:</span> " + donor.getFullName() + "</p>\n");
                writer.write("<p><span class=\"label\">Email:</span> " + donor.getEmail() + "</p>\n");
                writer.write("<p><span class=\"label\">Телефон:</span> " + donor.getPhone() + "</p>\n");
                writer.write("<p><span class=\"label\">Группа крови:</span> " + donor.getBloodGroup() + "</p>\n");
                writer.write("<p><span class=\"label\">Статус:</span> " + donor.getStatus() + "</p>\n");
                writer.write("<p><span class=\"label\">Последняя оценка:</span> " + 
                    (donor.getLastAssessmentDate() != null ? 
                    donor.getLastAssessmentDate().format(DATE_FORMATTER) : 
                    "Нет данных") + "</p>\n");
                writer.write("</div>\n");
                
                // Медицинская история
                writer.write("<h2>Медицинская история</h2>\n");
                writer.write("<div class=\"info\">\n");
                writer.write("<p>" + donor.getMedicalHistory().replace("\n", "<br>") + "</p>\n");
                writer.write("</div>\n");
                
                // Медицинские заметки
                writer.write("<h2>Медицинские заметки</h2>\n");
                writer.write("<div class=\"info\">\n");
                writer.write("<p>" + donor.getMedicalNotes().replace("\n", "<br>") + "</p>\n");
                writer.write("</div>\n");
                
                // Достижения
                writer.write("<h2>Достижения</h2>\n");
                writer.write("<ul class=\"achievements\">\n");
                List<Achievement> achievements = achievementService.getDonorAchievements(donor.getId().toString());
                for (Achievement achievement : achievements) {
                    String achievementText = String.format("%s %s (%s)",
                        achievement.getIcon(),
                        achievement.getTitle(),
                        achievement.getDateAwarded() != null ? 
                            achievement.getDateAwarded().format(DATE_FORMATTER) : "Не указана");
                    writer.write("<li>• " + achievementText + "</li>\n");
                }
                writer.write("</ul>\n");
                
                writer.write("</body>\n");
                writer.write("</html>");
            }

            // Открываем HTML в браузере по умолчанию
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(tempFile);
            }

        } catch (IOException e) {
            errorLabel.setText("Ошибка при создании документа: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
} 