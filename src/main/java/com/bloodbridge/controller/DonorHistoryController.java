package com.bloodbridge.controller;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Donation;
import com.bloodbridge.model.DonorAssessment;
import com.bloodbridge.service.DonationService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Контроллер для отображения истории донаций в системе BloodBridge.
 * Управляет отображением истории донаций донора.
 * Обеспечивает фильтрацию и поиск по истории.
 */
public class DonorHistoryController implements Initializable {
    @FXML private Label donorNameLabel;
    @FXML private Label donorBloodGroupLabel;
    
    @FXML private TableView<Donation> donationTable;
    @FXML private TableColumn<Donation, String> dateColumn;
    @FXML private TableColumn<Donation, String> statusColumn;
    @FXML private TableColumn<Donation, String> notesColumn;
    
    @FXML private TableView<DonorAssessment> assessmentsTable;
    @FXML private TableColumn<DonorAssessment, String> assessmentDateColumn;
    @FXML private TableColumn<DonorAssessment, String> resultColumn;
    @FXML private TableColumn<DonorAssessment, String> hemoglobinColumn;
    @FXML private TableColumn<DonorAssessment, String> pressureColumn;
    @FXML private TableColumn<DonorAssessment, String> assessmentNotesColumn;
    
    private Stage stage;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDonationsTable();
    }
    
    private void setupDonationsTable() {
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateTime().format(DATE_FORMATTER)
            )
        );
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
    }
    
    public void setDonor(Donor donor) {
        if (donor != null) {
            donorNameLabel.setText(donor.getFullName());
            donorBloodGroupLabel.setText(donor.getBloodGroup());
            donationTable.setItems(DonationService.getDonationsByDonor(donor));
        }
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void handleClose() {
        stage.close();
    }
} 