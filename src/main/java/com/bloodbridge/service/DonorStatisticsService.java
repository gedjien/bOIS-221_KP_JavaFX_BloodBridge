package com.bloodbridge.service;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Donation;
import com.bloodbridge.model.DonorStatistics;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

/**
 * Сервис для работы со статистикой доноров в системе BloodBridge.
 * Обеспечивает сбор и анализ данных о донациях.
 * Реализует бизнес-логику формирования статистических отчетов.
 */
public class DonorStatisticsService {
    private static final int MIN_DAYS_BETWEEN_DONATIONS = 60; // Минимальный интервал между донациями

    public static DonorStatistics calculateStatistics(Donor donor) {
        DonorStatistics statistics = new DonorStatistics();
        List<Donation> donorDonations = DonationService.getDonorDonations(donor);
        
        // Устанавливаем группу крови
        statistics.setBloodGroup(donor.getBloodGroup());
        
        // Подсчитываем общее количество донаций
        statistics.setTotalDonations(donorDonations.size());
        
        // Подсчитываем количество завершенных донаций
        long completedCount = donorDonations.stream()
            .filter(d -> DonationService.STATUS_COMPLETED.equals(d.getStatus()))
            .count();
        statistics.setCompletedDonations((int) completedCount);
        
        // Находим дату последней донации
        donorDonations.stream()
            .filter(d -> DonationService.STATUS_COMPLETED.equals(d.getStatus()))
            .max(Comparator.comparing(Donation::getDateTime))
            .ifPresent(donation -> {
                statistics.setLastDonationDate(donation.getDateTime());
                // Вычисляем следующую возможную дату донации
                LocalDateTime nextPossibleDate = donation.getDateTime()
                    .plus(MIN_DAYS_BETWEEN_DONATIONS, ChronoUnit.DAYS);
                statistics.setNextPossibleDonationDate(nextPossibleDate);
            });
        
        // Вычисляем серию донаций
        int currentStreak = 0;
        LocalDateTime lastDonationDate = null;
        
        List<Donation> sortedDonations = donorDonations.stream()
            .filter(d -> DonationService.STATUS_COMPLETED.equals(d.getStatus()))
            .sorted(Comparator.comparing(Donation::getDateTime).reversed())
            .toList();
        
        for (Donation donation : sortedDonations) {
            if (lastDonationDate == null) {
                currentStreak = 1;
                lastDonationDate = donation.getDateTime();
            } else {
                long daysBetween = ChronoUnit.DAYS.between(
                    donation.getDateTime(),
                    lastDonationDate
                );
                if (daysBetween <= MIN_DAYS_BETWEEN_DONATIONS + 30) { // Допускаем небольшой запас
                    currentStreak++;
                    lastDonationDate = donation.getDateTime();
                } else {
                    break;
                }
            }
        }
        
        statistics.setDonationStreak(currentStreak);
        
        return statistics;
    }
} 