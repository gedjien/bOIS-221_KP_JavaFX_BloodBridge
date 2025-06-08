package com.bloodbridge.service;

import com.bloodbridge.model.Staff;
import com.bloodbridge.util.JsonDataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления персоналом в системе BloodBridge.
 * Обеспечивает работу с данными медицинского персонала.
 * Реализует бизнес-логику управления персоналом.
 */
public class StaffService {
    private static final ObservableList<Staff> staffList = FXCollections.observableArrayList();
    private static Staff currentStaff;
    
    static {
        loadStaff();
    }
    
    private static void loadStaff() {
        List<Staff> loadedStaff = JsonDataManager.loadStaff();
        if (loadedStaff != null && !loadedStaff.isEmpty()) {
            staffList.setAll(loadedStaff);
        } else {
            // Добавляем тестового сотрудника, если список пуст
            Staff staff = new Staff();
            staff.setId(1L);
            staff.setFullName("Иванов Иван");
            staff.setEmail("staff@bloodbridge.com");
            staff.setPassword("staff@bloodbridge.com");
            staff.setRole("Врач");
            staffList.add(staff);
            JsonDataManager.saveStaff(staffList);
        }
    }
    
    public static boolean authenticateStaff(String email, String password) {
        return staffList.stream()
                .anyMatch(staff -> staff.getEmail().equals(email) && staff.getPassword().equals(password));
    }
    
    public static Staff authenticate(String email, String password) {
        System.out.println("Попытка аутентификации персонала:");
        System.out.println("Email: " + email);
        System.out.println("Пароль: " + password);
        
        Staff foundStaff = staffList.stream()
                .filter(staff -> staff.getEmail().equals(email) && staff.getPassword().equals(password))
                .findFirst()
                .orElse(null);
                
        if (foundStaff != null) {
            System.out.println("Персонал найден: " + foundStaff.getFullName());
        } else {
            System.out.println("Персонал не найден");
        }
        
        return foundStaff;
    }
    
    public static void setCurrentStaff(Staff staff) {
        currentStaff = staff;
    }
    
    public static Staff getCurrentStaff() {
        return currentStaff;
    }
    
    public static List<Staff> getAllStaff() {
        return staffList;
    }
    
    public static Staff getStaffById(Long id) {
        return staffList.stream()
                .filter(staff -> staff.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    public static Staff getStaffByEmail(String email) {
        return staffList.stream()
                .filter(staff -> staff.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }
    
    public static void addStaff(Staff staff) {
        staffList.add(staff);
        JsonDataManager.saveStaff(staffList);
    }
    
    public static void updateStaff(Staff staff) {
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getId().equals(staff.getId())) {
                staffList.set(i, staff);
                break;
            }
        }
        JsonDataManager.saveStaff(staffList);
    }
    
    public static void deleteStaff(Long id) {
        staffList.removeIf(staff -> staff.getId().equals(id));
        JsonDataManager.saveStaff(staffList);
    }
} 