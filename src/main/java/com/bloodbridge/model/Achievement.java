package com.bloodbridge.model;

import java.time.LocalDateTime;

/**
 * Класс, представляющий достижение донора в системе BloodBridge.
 * Содержит информацию о типе достижения и дате его получения.
 * Используется для мотивации доноров.
 */
public class Achievement {
    private String id;
    private String title;
    private String icon;
    private String description;
    private String donorId;
    private LocalDateTime dateAwarded;

    public Achievement() {
    }

    public Achievement(String id, String title, String icon, String description) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDonorId() {
        return donorId;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public LocalDateTime getDateAwarded() {
        return dateAwarded;
    }

    public void setDateAwarded(LocalDateTime dateAwarded) {
        this.dateAwarded = dateAwarded;
    }
} 