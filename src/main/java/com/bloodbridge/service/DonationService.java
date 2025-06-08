package com.bloodbridge.service;

import com.bloodbridge.model.Donation;
import com.bloodbridge.model.Donor;
import com.bloodbridge.util.JsonDataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Сервис для управления донациями в системе BloodBridge.
 * Обеспечивает регистрацию и учет донаций.
 * Реализует бизнес-логику работы с донациями.
 */
public class DonationService {
    private static ObservableList<Donation> donations = FXCollections.observableArrayList();
    private static final AtomicLong idGenerator = new AtomicLong(1);
    
    // Константы для статусов
    public static final String STATUS_SCHEDULED = "Запланирована";
    public static final String STATUS_CANCELLED = "Отменена";
    public static final String STATUS_COMPLETED = "Завершена";
    public static final String STATUS_MISSED = "Пропущена";

    static {
        loadDonations();
    }

    private static void loadDonations() {
        List<Donation> loadedDonations = JsonDataManager.loadDonations();
        if (loadedDonations != null && !loadedDonations.isEmpty()) {
            // Фильтруем некорректные донации и восстанавливаем связь с донорами
            List<Donation> validDonations = loadedDonations.stream()
                .filter(donation -> 
                    donation != null && 
                    donation.getId() != null && 
                    donation.getDonorId() != null && 
                    donation.getDateTime() != null && 
                    donation.getStatus() != null && 
                    !donation.getStatus().trim().isEmpty())
                .peek(donation -> {
                    // Восстанавливаем связь с донором
                    Donor donor = DonorService.getAllDonors().stream()
                        .filter(d -> d.getId().equals(donation.getDonorId()))
                        .findFirst()
                        .orElse(null);
                    donation.setDonor(donor);
                })
                .collect(Collectors.toList());
            
            donations.setAll(validDonations);
            
            // Обновляем генератор ID
            long maxId = validDonations.stream()
                .mapToLong(Donation::getId)
                .max()
                .orElse(0);
            idGenerator.set(maxId + 1);
        }
    }

    public static ObservableList<Donation> getAllDonations() {
        return donations;
    }

    public static ObservableList<Donation> getUpcomingDonations() {
        LocalDateTime now = LocalDateTime.now();
        return FXCollections.observableArrayList(
            donations.stream()
                .filter(d -> d != null && d.getDateTime() != null && 
                           d.getDateTime().isAfter(now) && 
                           STATUS_SCHEDULED.equals(d.getStatus()))
                .collect(Collectors.toList())
        );
    }

    public static ObservableList<Donation> getUpcomingDonations(Donor donor) {
        if (donor == null) {
            return FXCollections.observableArrayList();
        }
        LocalDateTime now = LocalDateTime.now();
        return FXCollections.observableArrayList(
            donations.stream()
                .filter(d -> d != null && d.getDonor() != null && 
                           d.getDonor().getId().equals(donor.getId()) && 
                           d.getDateTime() != null &&
                           d.getDateTime().isAfter(now) &&
                           STATUS_SCHEDULED.equals(d.getStatus()))
                .collect(Collectors.toList())
        );
    }

    public static List<Donation> getDonorDonations(Long donorId) {
        if (donorId == null) {
            return new ArrayList<>();
        }
        return donations.stream()
            .filter(d -> d != null && d.getDonor() != null && 
                        d.getDonor().getId().equals(donorId))
            .collect(Collectors.toList());
    }

    public static List<Donation> getDonorDonations(Donor donor) {
        if (donor == null) {
            return new ArrayList<>();
        }
        return donations.stream()
            .filter(d -> d != null && d.getDonor() != null && 
                        d.getDonor().equals(donor))
            .collect(Collectors.toList());
    }

    public static Donation getLastDonation(Long donorId) {
        if (donorId == null) {
            return null;
        }
        return donations.stream()
            .filter(d -> d != null && d.getDonor() != null && 
                        d.getDonor().getId().equals(donorId) && 
                        STATUS_COMPLETED.equals(d.getStatus()))
            .max((d1, d2) -> d1.getDateTime().compareTo(d2.getDateTime()))
            .orElse(null);
    }

    public static void confirmDonation(Long donationId) {
        if (donationId == null) {
            return;
        }
        donations.stream()
            .filter(d -> d != null && d.getId() != null && d.getId().equals(donationId))
            .findFirst()
            .ifPresent(d -> {
                d.setStatus(STATUS_COMPLETED);
                JsonDataManager.saveDonations(donations);
            });
    }

    public static void cancelDonation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID донации не может быть null");
        }
        
        donations.stream()
                .filter(donation -> donation != null && donation.getId() != null && 
                                  donation.getId().equals(id))
                .findFirst()
                .ifPresent(donation -> {
                    donation.setStatus(STATUS_CANCELLED);
                    JsonDataManager.saveDonations(donations);
                });
    }

    public static void addDonation(Donation donation) {
        System.out.println("Добавление новой донации...");
        System.out.println("ID донора: " + donation.getDonorId());
        System.out.println("Статус: " + donation.getStatus());
        
        if (donation == null) {
            throw new IllegalArgumentException("Донация не может быть null");
        }
        
        if (donation.getDonor() == null) {
            throw new IllegalArgumentException("Донор не может быть null");
        }
        
        if (donation.getDateTime() == null) {
            throw new IllegalArgumentException("Дата и время не могут быть null");
        }
        
        if (donation.getStatus() == null || donation.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Статус не может быть пустым");
        }
        
        if (donation.getBloodVolume() <= 0) {
            throw new IllegalArgumentException("Объем крови должен быть больше 0");
        }
        
        // Проверяем, что донор существует
        if (!DonorService.getAllDonors().contains(donation.getDonor())) {
            throw new IllegalArgumentException("Указанный донор не существует в системе");
        }
        
        // Генерируем уникальный ID для новой донации
        donation.setId(idGenerator.getAndIncrement());
        
        // Добавляем донацию в список
        donations.add(donation);
        
        // Сохраняем изменения
        JsonDataManager.saveDonations(donations);
        System.out.println("Донация успешно сохранена");
    }

    public static void updateDonation(Donation donation) {
        if (donation == null || donation.getId() == null) {
            throw new IllegalArgumentException("Донация или её ID не могут быть null");
        }
        
        // Находим и обновляем донацию
        for (int i = 0; i < donations.size(); i++) {
            if (donations.get(i) != null && donations.get(i).getId() != null && 
                donations.get(i).getId().equals(donation.getId())) {
                donations.set(i, donation);
                // Сохраняем изменения
                JsonDataManager.saveDonations(donations);
                return;
            }
        }
        
        throw new IllegalArgumentException("Донация с указанным ID не найдена");
    }

    public static void deleteDonation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID донации не может быть null");
        }
        
        // Находим и удаляем донацию
        donations.removeIf(donation -> donation != null && donation.getId() != null && 
                                   donation.getId().equals(id));
        
        // Сохраняем изменения
        JsonDataManager.saveDonations(donations);
    }

    public static void markAsMissed(Long donationId) {
        if (donationId == null) {
            return;
        }
        donations.stream()
            .filter(d -> d != null && d.getId() != null && d.getId().equals(donationId))
            .findFirst()
            .ifPresent(donation -> {
                donation.setStatus(STATUS_MISSED);
                JsonDataManager.saveDonations(donations);
            });
    }

    public static List<Donation> getDonationsByDonorId(Long donorId) {
        if (donorId == null) {
            return new ArrayList<>();
        }
        return donations.stream()
                .filter(donation -> donation != null && donation.getDonor() != null && 
                                  donation.getDonor().getId().equals(donorId))
                .collect(Collectors.toList());
    }

    public static ObservableList<Donation> getDonationsByDonor(Donor donor) {
        if (donor == null) {
            throw new IllegalArgumentException("Донор не может быть null");
        }
        
        return donations.stream()
                .filter(donation -> donation != null && donation.getDonor() != null && 
                                  donation.getDonor().equals(donor))
                .collect(Collectors.collectingAndThen(
                    Collectors.toList(),
                    FXCollections::observableArrayList
                ));
    }

    public static ObservableList<Donation> getDonationsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Статус не может быть пустым");
        }
        
        return donations.stream()
                .filter(donation -> donation != null && donation.getStatus() != null && 
                                  donation.getStatus().equals(status))
                .collect(Collectors.collectingAndThen(
                    Collectors.toList(),
                    FXCollections::observableArrayList
                ));
    }

    public static void removeDonation(Donation donation) {
        donations.remove(donation);
    }
} 