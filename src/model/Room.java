package model;

import java.io.Serializable;

public class Room implements Serializable {
    private String id;
    private String name;
    private String type; // Deluxe, Standard, Suite, ...
    private double dailyRate;
    private int capacity;
    private String furniture;
    private boolean occupied;

    public Room(String id, String name, String type, double dailyRate, int capacity, String furniture) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dailyRate = dailyRate;
        this.capacity = capacity;
        this.furniture = furniture;
        this.occupied = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getDailyRate() { return dailyRate; }
    public int getCapacity() { return capacity; }
    public String getFurniture() { return furniture; }
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }

    @Override
    public String toString() {
        return String.format("%-6s | %-15s | %-8s | %7.2f | %8d | %s",
                id, name, type, dailyRate, capacity, furniture);
    }
}