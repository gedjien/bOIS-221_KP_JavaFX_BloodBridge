package com.bloodbridge.service;

import com.bloodbridge.model.DonationBooking;
import com.bloodbridge.model.Donation;
import com.bloodbridge.util.JsonDataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления записями на донацию в системе BloodBridge.
 * Обеспечивает бронирование времени для донаций.
 * Реализует логику управления расписанием донаций.
 */
public class DonationBookingService {
    private static final ObservableList<DonationBooking> bookings = FXCollections.observableArrayList();
    private static final int MAX_BOOKINGS_PER_DAY = 10;
    private static final int BOOKING_INTERVAL_MINUTES = 30;

    static {
        // Загружаем записи из JSON при старте
        bookings.addAll(JsonDataManager.loadBookings());
    }

    public static ObservableList<DonationBooking> getAllBookings() {
        return bookings;
    }

    public ObservableList<DonationBooking> getDonorBookings(Long donorId) {
        return bookings.filtered(booking -> booking.getDonorId().equals(donorId));
    }

    public ObservableList<DonationBooking> getUpcomingBookings() {
        LocalDateTime now = LocalDateTime.now();
        return bookings.filtered(booking -> 
            booking.getBookingDateTime().isAfter(now) && 
            DonationService.STATUS_SCHEDULED.equals(booking.getStatus())
        );
    }

    public boolean isTimeSlotAvailable(LocalDateTime dateTime) {
        // Проверяем количество записей на этот день
        long bookingsOnDay = bookings.stream()
            .filter(booking -> 
                booking.getBookingDateTime().toLocalDate().equals(dateTime.toLocalDate()) &&
                DonationService.STATUS_SCHEDULED.equals(booking.getStatus())
            )
            .count();

        if (bookingsOnDay >= MAX_BOOKINGS_PER_DAY) {
            return false;
        }

        // Проверяем, нет ли уже записи в этот временной слот
        return bookings.stream()
            .noneMatch(booking -> 
                booking.getBookingDateTime().equals(dateTime) &&
                DonationService.STATUS_SCHEDULED.equals(booking.getStatus())
            );
    }

    public void addBooking(DonationBooking booking) {
        if (!isTimeSlotAvailable(booking.getBookingDateTime())) {
            throw new IllegalArgumentException("Выбранное время недоступно");
        }

        booking.setId(System.currentTimeMillis());
        booking.setStatus(DonationService.STATUS_SCHEDULED);
        bookings.add(booking);
        JsonDataManager.saveBookings(bookings);

        // Создаем соответствующую запись в DonationService
        Donation donation = new Donation();
        donation.setId(booking.getId());
        donation.setDonor(DonorService.getCurrentDonor());
        donation.setDateTime(booking.getBookingDateTime());
        donation.setStatus(DonationService.STATUS_SCHEDULED);
        donation.setNotes(booking.getNotes());
        DonationService.addDonation(donation);
    }

    public void updateBooking(DonationBooking booking) {
        int index = bookings.indexOf(booking);
        if (index != -1) {
            bookings.set(index, booking);
            JsonDataManager.saveBookings(bookings);

            // Обновляем соответствующую запись в DonationService
            Donation donation = new Donation();
            donation.setId(booking.getId());
            donation.setDonor(DonorService.getCurrentDonor());
            donation.setDateTime(booking.getBookingDateTime());
            donation.setStatus(booking.getStatus());
            donation.setNotes(booking.getNotes());
            DonationService.updateDonation(donation);
        }
    }

    public void cancelBooking(Long bookingId) {
        bookings.stream()
            .filter(booking -> booking.getId().equals(bookingId))
            .findFirst()
            .ifPresent(booking -> {
                booking.setStatus(DonationService.STATUS_CANCELLED);
                JsonDataManager.saveBookings(bookings);
                // Отменяем соответствующую запись в DonationService
                DonationService.cancelDonation(bookingId);
            });
    }

    public List<LocalDateTime> getAvailableTimeSlots(LocalDateTime date) {
        LocalDateTime startTime = date.toLocalDate().atTime(9, 0); // Начало рабочего дня
        LocalDateTime endTime = date.toLocalDate().atTime(18, 0); // Конец рабочего дня

        return startTime.toLocalDate().datesUntil(endTime.toLocalDate().plusDays(1))
            .flatMap(day -> {
                LocalDateTime slot = day.atTime(9, 0);
                return java.util.stream.Stream.iterate(slot, 
                    time -> time.isBefore(day.atTime(18, 0)),
                    time -> time.plusMinutes(BOOKING_INTERVAL_MINUTES)
                );
            })
            .filter(this::isTimeSlotAvailable)
            .collect(Collectors.toList());
    }
} 