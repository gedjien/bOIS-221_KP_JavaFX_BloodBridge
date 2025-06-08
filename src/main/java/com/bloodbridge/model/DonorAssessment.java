package com.bloodbridge.model;

import java.time.LocalDateTime;

/**
 * Класс, представляющий оценку состояния донора в системе BloodBridge.
 * Содержит медицинские показатели и результаты оценки.
 * Используется для определения пригодности донора.
 */
public class DonorAssessment {
    private int id;
    private Long donorId;
    private LocalDateTime assessmentDate;
    private String bloodPressure;
    private int systolicPressure;
    private int diastolicPressure;
    private int pulse;
    private double temperature;
    private boolean hasFever;
    private boolean hasCold;
    private boolean hasRecentSurgery;
    private boolean hasPregnancy;
    private boolean isEligible;
    private double hemoglobin;
    private double weight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public LocalDateTime getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDateTime assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public int getSystolicPressure() {
        return systolicPressure;
    }

    public void setSystolicPressure(int systolicPressure) {
        this.systolicPressure = systolicPressure;
    }

    public int getDiastolicPressure() {
        return diastolicPressure;
    }

    public void setDiastolicPressure(int diastolicPressure) {
        this.diastolicPressure = diastolicPressure;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean isHasFever() {
        return hasFever;
    }

    public void setHasFever(boolean hasFever) {
        this.hasFever = hasFever;
    }

    public boolean isHasCold() {
        return hasCold;
    }

    public void setHasCold(boolean hasCold) {
        this.hasCold = hasCold;
    }

    public boolean isHasRecentSurgery() {
        return hasRecentSurgery;
    }

    public void setHasRecentSurgery(boolean hasRecentSurgery) {
        this.hasRecentSurgery = hasRecentSurgery;
    }

    public boolean isHasPregnancy() {
        return hasPregnancy;
    }

    public void setHasPregnancy(boolean hasPregnancy) {
        this.hasPregnancy = hasPregnancy;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }

    public double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
} 