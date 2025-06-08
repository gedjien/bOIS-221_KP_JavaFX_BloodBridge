package com.bloodbridge.model;

/**
 * Класс, представляющий сотрудника в системе BloodBridge.
 * Содержит информацию о медицинском персонале.
 * Используется для управления персоналом и правами доступа.
 */
public class Staff {
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String role;

    public Staff() {
    }

    public Staff(Long id, String fullName, String email, String password, String role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
} 