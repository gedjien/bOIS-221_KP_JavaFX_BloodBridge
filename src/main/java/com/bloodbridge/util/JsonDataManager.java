package com.bloodbridge.util;

import com.bloodbridge.model.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonDataManager {
    private static final String DATA_DIR = "data";
    private static final String DONORS_FILE = DATA_DIR + "/donors.json";
    private static final String DONATIONS_FILE = DATA_DIR + "/donations.json";
    private static final String STAFF_FILE = DATA_DIR + "/staff.json";
    private static final String BOOKINGS_FILE = DATA_DIR + "/bookings.json";
    private static final String ACHIEVEMENTS_FILE = DATA_DIR + "/achievements.json";
    private static final String DONOR_ACHIEVEMENTS_FILE = DATA_DIR + "/donor_achievements.json";
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    static {
        createDataDirectory();
    }

    private static void createDataDirectory() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static ObservableList<Donor> loadDonors() {
        try {
            File file = new File(DONORS_FILE);
            if (!file.exists()) {
                return FXCollections.observableArrayList();
            }
            List<Donor> donors = objectMapper.readValue(file, new TypeReference<List<Donor>>() {});
            return FXCollections.observableArrayList(donors);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static void saveDonors(List<Donor> donors) {
        try {
            objectMapper.writeValue(new File(DONORS_FILE), donors);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Donation> loadDonations() {
        try {
            File file = new File(DONATIONS_FILE);
            if (!file.exists()) {
                return FXCollections.observableArrayList();
            }
            List<Donation> donations = objectMapper.readValue(file, new TypeReference<List<Donation>>() {});
            return FXCollections.observableArrayList(donations);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static void saveDonations(List<Donation> donations) {
        try {
            objectMapper.writeValue(new File(DONATIONS_FILE), donations);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Staff> loadStaff() {
        try {
            File file = new File(STAFF_FILE);
            if (!file.exists()) {
                return FXCollections.observableArrayList();
            }
            List<Staff> staff = objectMapper.readValue(file, new TypeReference<List<Staff>>() {});
            return FXCollections.observableArrayList(staff);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static void saveStaff(List<Staff> staff) {
        try {
            objectMapper.writeValue(new File(STAFF_FILE), staff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<DonationBooking> loadBookings() {
        try {
            File file = new File(BOOKINGS_FILE);
            if (!file.exists()) {
                return FXCollections.observableArrayList();
            }
            List<DonationBooking> bookings = objectMapper.readValue(file, new TypeReference<List<DonationBooking>>() {});
            return FXCollections.observableArrayList(bookings);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static void saveBookings(List<DonationBooking> bookings) {
        try {
            objectMapper.writeValue(new File(BOOKINGS_FILE), bookings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Achievement> loadAchievements() {
        try {
            File file = new File(ACHIEVEMENTS_FILE);
            if (!file.exists()) {
                return FXCollections.observableArrayList();
            }
            List<Achievement> achievements = objectMapper.readValue(file, new TypeReference<List<Achievement>>() {});
            return FXCollections.observableArrayList(achievements);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static void saveAchievements(List<Achievement> achievements) {
        try {
            objectMapper.writeValue(new File(ACHIEVEMENTS_FILE), achievements);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Achievement> loadDonorAchievements() {
        try {
            File file = new File(DONOR_ACHIEVEMENTS_FILE);
            System.out.println("Загрузка достижений доноров из файла: " + file.getAbsolutePath());
            if (!file.exists()) {
                System.out.println("Файл не существует, возвращаем пустой список");
                return FXCollections.observableArrayList();
            }
            List<Achievement> achievements = objectMapper.readValue(file, new TypeReference<List<Achievement>>() {});
            System.out.println("Загружено достижений: " + achievements.size());
            return FXCollections.observableArrayList(achievements);
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке достижений: " + e.getMessage());
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }

    public static void saveDonorAchievements(List<Achievement> achievements) {
        try {
            System.out.println("Сохранение достижений доноров. Количество: " + achievements.size());
            File file = new File(DONOR_ACHIEVEMENTS_FILE);
            objectMapper.writeValue(file, achievements);
            System.out.println("Достижения успешно сохранены в файл: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Ошибка при сохранении достижений: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 