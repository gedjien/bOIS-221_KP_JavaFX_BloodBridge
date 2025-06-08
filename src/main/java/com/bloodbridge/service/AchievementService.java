package com.bloodbridge.service;

import com.bloodbridge.model.Achievement;
import com.bloodbridge.model.Donor;
import com.bloodbridge.model.DonorStatistics;
import com.bloodbridge.util.JsonDataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * Сервис для управления достижениями в системе BloodBridge.
 * Обеспечивает выдачу и отслеживание достижений доноров.
 * Реализует логику мотивационной системы.
 */
public class AchievementService {
    private static final String ACHIEVEMENTS_FILE = "data/achievements.json";
    private static final String DONOR_ACHIEVEMENTS_FILE = "data/donor_achievements.json";
    private final JsonDataManager jsonDataManager;
    private final ObservableList<Achievement> achievements;
    private final ObservableList<Achievement> donorAchievements;

    public AchievementService() {
        this.jsonDataManager = new JsonDataManager();
        this.achievements = FXCollections.observableArrayList();
        this.donorAchievements = FXCollections.observableArrayList();
        initializeAchievements();
    }

    private void initializeAchievements() {
        List<Achievement> loadedAchievements = jsonDataManager.loadAchievements();
        if (loadedAchievements == null || loadedAchievements.isEmpty()) {
            createDefaultAchievements();
            jsonDataManager.saveAchievements(achievements);
        } else {
            achievements.setAll(loadedAchievements);
        }

        List<Achievement> loadedDonorAchievements = jsonDataManager.loadDonorAchievements();
        if (loadedDonorAchievements != null) {
            donorAchievements.setAll(loadedDonorAchievements);
        }
    }

    private void createDefaultAchievements() {
        achievements.add(new Achievement("1", "Первая донация", "🏆", "Сделал первую донацию крови"));
        achievements.add(new Achievement("2", "Ветеран", "⭐", "Сделал 5 донаций"));
        achievements.add(new Achievement("3", "Герой", "👑", "Сделал 10 донаций"));
        achievements.add(new Achievement("4", "Регулярный донор", "🔄", "Сделал донацию в течение 3 месяцев"));
        achievements.add(new Achievement("5", "Спаситель", "❤️", "Сделал донацию в экстренной ситуации"));
    }

    public List<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements);
    }

    public List<Achievement> getDonorAchievements(String donorId) {
        System.out.println("Получение достижений для донора ID: " + donorId);
        List<Achievement> achievements = donorAchievements.stream()
                .filter(a -> a.getDonorId().equals(donorId))
                .collect(Collectors.toList());
        System.out.println("Найдено достижений: " + achievements.size());
        return achievements;
    }

    public ObservableList<Achievement> getDonorAchievements(Donor donor) {
        if (donor == null) {
            return FXCollections.observableArrayList();
        }

        DonorStatistics statistics = DonorStatisticsService.calculateStatistics(donor);
        List<Achievement> earnedAchievements = new ArrayList<>();

        // Проверяем достижения за количество донаций
        if (statistics.getCompletedDonations() >= 1) {
            Achievement achievement = findAchievement("1"); // Первая донация
            if (achievement != null) {
                achievement.setDonorId(donor.getId().toString());
                achievement.setDateAwarded(LocalDateTime.now());
                earnedAchievements.add(achievement);
            }
        }
        if (statistics.getCompletedDonations() >= 5) {
            Achievement achievement = findAchievement("2"); // Ветеран
            if (achievement != null) {
                achievement.setDonorId(donor.getId().toString());
                achievement.setDateAwarded(LocalDateTime.now());
                earnedAchievements.add(achievement);
            }
        }
        if (statistics.getCompletedDonations() >= 10) {
            Achievement achievement = findAchievement("3"); // Герой
            if (achievement != null) {
                achievement.setDonorId(donor.getId().toString());
                achievement.setDateAwarded(LocalDateTime.now());
                earnedAchievements.add(achievement);
            }
        }

        // Проверяем достижения за регулярность
        if (statistics.getLastDonationDate() != null && 
            statistics.getLastDonationDate().plusMonths(3).isAfter(LocalDateTime.now())) {
            Achievement achievement = findAchievement("4"); // Регулярный донор
            if (achievement != null) {
                achievement.setDonorId(donor.getId().toString());
                achievement.setDateAwarded(LocalDateTime.now());
                earnedAchievements.add(achievement);
            }
        }

        // Проверяем достижения за редкую группу крови
        if (isRareBloodGroup(donor.getBloodGroup())) {
            Achievement achievement = findAchievement("5"); // Спаситель
            if (achievement != null) {
                achievement.setDonorId(donor.getId().toString());
                achievement.setDateAwarded(LocalDateTime.now());
                earnedAchievements.add(achievement);
            }
        }

        return FXCollections.observableArrayList(earnedAchievements);
    }

    public void awardAchievement(String donorId, String achievementId) {
        System.out.println("Выдача достижения. DonorId: " + donorId + ", AchievementId: " + achievementId);
        Achievement achievement = findAchievement(achievementId);
        if (achievement != null) {
            // Проверяем, не выдано ли уже это достижение
            boolean alreadyAwarded = donorAchievements.stream()
                .anyMatch(a -> a.getDonorId().equals(donorId) && a.getId().equals(achievementId));
            
            System.out.println("Достижение уже выдано: " + alreadyAwarded);
            
            if (!alreadyAwarded) {
                Achievement donorAchievement = new Achievement(
                    achievement.getId(),
                    achievement.getTitle(),
                    achievement.getIcon(),
                    achievement.getDescription()
                );
                donorAchievement.setDonorId(donorId);
                donorAchievement.setDateAwarded(LocalDateTime.now());
                donorAchievements.add(donorAchievement);
                System.out.println("Достижение добавлено в список");
                jsonDataManager.saveDonorAchievements(donorAchievements);
                System.out.println("Достижение сохранено в файл");
            }
        } else {
            System.out.println("Достижение не найдено!");
        }
    }

    private Achievement findAchievement(String id) {
        return achievements.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private boolean isRareBloodGroup(String bloodGroup) {
        return bloodGroup != null && (bloodGroup.equals("AB-") || bloodGroup.equals("B-"));
    }
} 