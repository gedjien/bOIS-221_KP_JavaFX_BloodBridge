package com.bloodbridge.model;

import java.time.LocalDateTime;

public class DonorStatistics {
    private int totalDonations;
    private int completedDonations;
    private LocalDateTime lastDonationDate;
    private LocalDateTime nextPossibleDonationDate;
    private String bloodGroup;
    private int donationStreak; // Подряд выполненных донаций

    public DonorStatistics() {
        this.totalDonations = 0;
        this.completedDonations = 0;
        this.donationStreak = 0;
    }

    public int getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(int totalDonations) {
        this.totalDonations = totalDonations;
    }

    public int getCompletedDonations() {
        return completedDonations;
    }

    public void setCompletedDonations(int completedDonations) {
        this.completedDonations = completedDonations;
    }

    public LocalDateTime getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDateTime lastDonationDate) {
        this.lastDonationDate = lastDonationDate;
    }

    public LocalDateTime getNextPossibleDonationDate() {
        return nextPossibleDonationDate;
    }

    public void setNextPossibleDonationDate(LocalDateTime nextPossibleDonationDate) {
        this.nextPossibleDonationDate = nextPossibleDonationDate;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getDonationStreak() {
        return donationStreak;
    }

    public void setDonationStreak(int donationStreak) {
        this.donationStreak = donationStreak;
    }
} 