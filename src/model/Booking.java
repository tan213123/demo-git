package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking implements Serializable {
    private String id; // nationalID + roomId + startDate
    private String nationalId;
    private String roomId;
    private LocalDate startDate;
    private int rentalDays;
    private LocalDate checkOutDate; // computed
    private Status status;
    private double amount; // cached total

    public enum Status { BOOKED, CHECKED_IN, CHECKED_OUT, CANCELED }

    public Booking(String nationalId, String roomId, LocalDate startDate, int rentalDays) {
        this.nationalId = nationalId;
        this.roomId = roomId;
        this.startDate = startDate;
        this.rentalDays = rentalDays;
        this.checkOutDate = startDate.plusDays(rentalDays);
        this.status = Status.BOOKED;
        this.id = nationalId + "-" + roomId + "-" + startDate;
        this.amount = 0.0;
    }

    public String getId() { return id; }
    public String getNationalId() { return nationalId; }
    public String getRoomId() { return roomId; }
    public LocalDate getStartDate() { return startDate; }
    public int getRentalDays() { return rentalDays; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public boolean isActiveOn(LocalDate date) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) && date.isBefore(checkOutDate);
    }
}