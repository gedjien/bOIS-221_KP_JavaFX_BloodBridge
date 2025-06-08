package com.bloodbridge.model;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;

/**
 * Класс, представляющий донацию крови в системе BloodBridge.
 * Содержит информацию о дате, типе и статусе донации.
 * Используется для отслеживания истории донаций донора.
 */
public class Donation {
    @Expose
    private Long id;
    @Expose
    private Long donorId; // Добавляем поле для хранения ID донора
    private transient Donor donor; // Делаем поле donor временным
    @Expose
    private LocalDateTime dateTime;
    @Expose
    private String status;
    @Expose
    private String notes;
    @Expose
    private int bloodVolume; // Объем крови в мл

    public Donation() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
        this.donorId = donor != null ? donor.getId() : null;
    }

    public Long getDonorId() {
        return donorId != null ? donorId : (donor != null ? donor.getId() : null);
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getBloodVolume() {
        return bloodVolume;
    }

    public void setBloodVolume(int bloodVolume) {
        this.bloodVolume = bloodVolume;
    }
} 