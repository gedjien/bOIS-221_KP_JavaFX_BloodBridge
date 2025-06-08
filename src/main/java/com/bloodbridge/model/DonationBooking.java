package com.bloodbridge.model;

import java.time.LocalDateTime;

/**
 * Класс, представляющий запись на донацию в системе BloodBridge.
 * Содержит информацию о запланированной донации.
 * Используется для управления расписанием донаций.
 */
public class DonationBooking {
    private Long id;
    private Long donorId;
    private LocalDateTime bookingDateTime;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    private String notes;

    public DonationBooking() {}

    public DonationBooking(Long id, Long donorId, LocalDateTime bookingDateTime, String status, String notes) {
        this.id = id;
        this.donorId = donorId;
        this.bookingDateTime = bookingDateTime;
        this.status = status;
        this.notes = notes;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public void setBookingDateTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
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
} 