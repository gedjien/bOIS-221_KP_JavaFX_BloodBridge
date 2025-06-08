package com.bloodbridge.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.time.LocalDateTime;

/**
 * Класс, представляющий донора крови в системе BloodBridge.
 * Содержит персональные данные, медицинскую информацию и историю донаций.
 * Используется для хранения и управления информацией о донорах.
 */
public class Donor {
    @Expose
    @SerializedName("id")
    private Long id;
    
    @Expose
    @SerializedName("fullName")
    private String fullName;
    
    @Expose
    @SerializedName("email")
    private String email;
    
    @Expose
    @SerializedName("phone")
    private String phone;
    
    @Expose
    @SerializedName("password")
    private String password;
    
    @Expose
    @SerializedName("bloodGroup")
    private String bloodGroup;
    
    @Expose
    @SerializedName("medicalHistory")
    private String medicalHistory;
    
    @Expose
    @SerializedName("medicalNotes")
    private String medicalNotes;
    
    @Expose
    @SerializedName("lastAssessmentDate")
    private LocalDateTime lastAssessmentDate;
    
    @Expose
    @SerializedName("status")
    private String status;

    public Donor() {
        this.status = "Активен";
        this.medicalHistory = "";
        this.medicalNotes = "";
        this.bloodGroup = "Не указана";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName != null ? fullName : "";
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password != null ? password : "";
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBloodGroup() {
        return bloodGroup != null ? bloodGroup : "Не указана";
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getMedicalHistory() {
        return medicalHistory != null ? medicalHistory : "";
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getMedicalNotes() {
        return medicalNotes != null ? medicalNotes : "";
    }

    public void setMedicalNotes(String medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public LocalDateTime getLastAssessmentDate() {
        return lastAssessmentDate;
    }

    public void setLastAssessmentDate(LocalDateTime lastAssessmentDate) {
        this.lastAssessmentDate = lastAssessmentDate;
    }

    public String getStatus() {
        return status != null ? status : "Активен";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donor donor = (Donor) o;
        return id != null && id.equals(donor.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 