package com.bloodbridge.service;

import com.bloodbridge.model.Donor;
import com.bloodbridge.model.Donation;
import com.bloodbridge.util.JsonDataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Тестовый класс для проверки функциональности DonorService
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DonorServiceTest {
    private Donor testDonor;
    private MockedStatic<JsonDataManager> mockedJsonDataManager;
    private MockedStatic<DonationService> mockedDonationService;

    @BeforeAll
    void initMocks() {
        mockedJsonDataManager = Mockito.mockStatic(JsonDataManager.class);
        mockedJsonDataManager.when(JsonDataManager::loadDonors)
                .thenReturn(FXCollections.observableArrayList());
        mockedJsonDataManager.when(() -> JsonDataManager.saveDonors(anyList())).then(invocation -> null);

        mockedDonationService = Mockito.mockStatic(DonationService.class);
        mockedDonationService.when(DonationService::getAllDonations)
                .thenReturn(FXCollections.observableArrayList());
    }

    @AfterAll
    void closeMocks() {
        mockedJsonDataManager.close();
        mockedDonationService.close();
    }

    @BeforeEach
    void setUp() {
        DonorService.getAllDonors().clear();
        DonorService.logout();
        testDonor = new Donor();
        testDonor.setFullName("Иван Иванов Иванович");
        testDonor.setEmail("ivan@example.com");
        testDonor.setPhone("1234567890");
        testDonor.setPassword("password123");
        testDonor.setBloodGroup("A+");
    }

    @Test
    void testAddDonor() {
        Donor donor = testDonor;
        DonorService.addDonor(donor);
        List<Donor> donors = DonorService.getAllDonors();
        assertTrue(donors.contains(donor));
        assertNotNull(donor.getId());
    }

    @Test
    void testUpdateDonor() {
        DonorService.addDonor(testDonor);
        String newPhone = "9876543210";
        testDonor.setPhone(newPhone);
        DonorService.updateDonor(testDonor);
        List<Donor> donors = DonorService.getAllDonors();
        Donor updatedDonor = donors.stream()
            .filter(d -> d.getId() == testDonor.getId())
            .findFirst()
            .orElse(null);
        assertNotNull(updatedDonor);
        assertEquals(newPhone, updatedDonor.getPhone());
    }

    @Test
    void testRemoveDonor() {
        DonorService.addDonor(testDonor);
        DonorService.removeDonor(testDonor);
        List<Donor> donors = DonorService.getAllDonors();
        assertFalse(donors.contains(testDonor));
    }

    @Test
    void testGetCurrentDonor() {
        DonorService.addDonor(testDonor);
        DonorService.setCurrentDonor(testDonor);
        Donor currentDonor = DonorService.getCurrentDonor();
        assertNotNull(currentDonor);
        assertEquals(testDonor.getId(), currentDonor.getId());
    }

    @Test
    void testLogout() {
        DonorService.addDonor(testDonor);
        DonorService.setCurrentDonor(testDonor);
        DonorService.logout();
        assertNull(DonorService.getCurrentDonor());
    }

    @Test
    void testGetDonorDonations() {
        DonorService.addDonor(testDonor);
        ObservableList<Donation> mockDonations = FXCollections.observableArrayList();
        Donation donation = new Donation();
        donation.setDonor(testDonor);
        donation.setDateTime(LocalDateTime.now());
        donation.setStatus("Успешно");
        donation.setBloodVolume(450);
        mockDonations.add(donation);
        mockedDonationService.when(DonationService::getAllDonations).thenReturn(mockDonations);
        List<Donation> donations = DonorService.getDonorDonations(testDonor);
        assertNotNull(donations);
        assertFalse(donations.isEmpty());
        assertEquals(1, donations.size());
    }

    @Test
    void testValidateDonorData() {
        String result = DonorService.validateDonorData("", "test@test.com", "1234567890", "A");
        assertNotNull(result);
        assertTrue(result.contains("Имя не может быть пустым"));
        result = DonorService.validateDonorData("Иван", "", "1234567890", "A");
        assertNotNull(result);
        assertTrue(result.contains("Email не может быть пустым"));
        result = DonorService.validateDonorData("Иван", "test@test.com", "", "A");
        assertNotNull(result);
        assertTrue(result.contains("Телефон не может быть пустым"));
        result = DonorService.validateDonorData("Иван", "test@test.com", "1234567890", "");
        assertNotNull(result);
        assertTrue(result.contains("Группа крови не может быть пустой"));
        result = DonorService.validateDonorData("Иван", "test@test.com", "1234567890", "A");
        assertNull(result);
    }

    @Test
    void testAuthenticateDonor() {
        DonorService.addDonor(testDonor);
        Optional<Donor> authenticatedDonor = DonorService.authenticateDonor(
            testDonor.getEmail(),
            testDonor.getPassword()
        );
        assertTrue(authenticatedDonor.isPresent());
        assertEquals(testDonor.getId(), authenticatedDonor.get().getId());
        assertEquals(testDonor, DonorService.getCurrentDonor());
    }

    @Test
    void testAuthenticateDonorWithInvalidCredentials() {
        DonorService.addDonor(testDonor);
        Optional<Donor> authenticatedDonor = DonorService.authenticateDonor(
            testDonor.getEmail(),
            "wrongpassword"
        );
        assertFalse(authenticatedDonor.isPresent());
        assertNull(DonorService.getCurrentDonor());
    }
} 