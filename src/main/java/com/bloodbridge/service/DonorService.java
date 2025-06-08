package com.bloodbridge.service;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Donation;
import com.bloodbridge.util.JsonDataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для управления донорами в системе BloodBridge.
 * Обеспечивает CRUD операции с данными доноров.
 * Реализует бизнес-логику работы с донорами.
 */
public class DonorService {
    private static final ObservableList<Donor> donors = FXCollections.observableArrayList();
    private static Donor currentDonor;

    static {
        loadDonors();
    }

    private static void loadDonors() {
        List<Donor> loadedDonors = JsonDataManager.loadDonors();
        if (loadedDonors != null) {
            donors.setAll(loadedDonors);
        }
    }

    public static List<Donor> getAllDonors() {
        return donors;
    }

    public static void addDonor(Donor donor) {
        donor.setId(System.currentTimeMillis());
        donors.add(donor);
        JsonDataManager.saveDonors(donors);
    }

    public static void updateDonor(Donor donor) {
        for (int i = 0; i < donors.size(); i++) {
            if (donors.get(i).getId() == donor.getId()) {
                donors.set(i, donor);
                break;
            }
        }
        JsonDataManager.saveDonors(donors);
    }

    public static void removeDonor(Donor donor) {
        donors.remove(donor);
        JsonDataManager.saveDonors(donors);
    }

    public static Donor getCurrentDonor() {
        return currentDonor;
    }

    public static void setCurrentDonor(Donor donor) {
        currentDonor = donor;
    }

    public static void logout() {
        currentDonor = null;
    }

    public static List<Donation> getDonorDonations(Donor donor) {
        return DonationService.getAllDonations().stream()
            .filter(donation -> donation.getDonor().getId() == donor.getId())
            .collect(Collectors.toList());
    }

    public static String validateDonorData(String name, String email, String phone, String bloodGroup) {
        if (name == null || name.trim().isEmpty()) {
            return "Имя не может быть пустым";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email не может быть пустым";
        }
        if (phone == null || phone.trim().isEmpty()) {
            return "Телефон не может быть пустым";
        }
        if (bloodGroup == null || bloodGroup.trim().isEmpty()) {
            return "Группа крови не может быть пустой";
        }
        return null;
    }

    public static Optional<Donor> authenticateDonor(String email, String password) {
        Optional<Donor> donor = donors.stream()
            .filter(d -> d.getEmail().equals(email) && d.getPassword().equals(password))
            .findFirst();
            
        if (donor.isPresent()) {
            currentDonor = donor.get();
        }
        
        return donor;
    }
} 