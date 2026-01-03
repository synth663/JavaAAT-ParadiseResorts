package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Booking model representing a resort reservation.
 */
public class Booking {
    private int id;
    private int userId;
    private int resortId;
    private int roomId;
    private Integer foodOptionId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numGuests;
    private String status;
    private LocalDateTime createdAt;

    // Transient fields for display
    private String resortName;
    private String roomType;
    private String cuisineType;
    private String mealPlan;
    private int beds;
    private String username;

    public Booking() {
        this.status = "confirmed";
        this.numGuests = 1;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getResortId() {
        return resortId;
    }

    public void setResortId(int resortId) {
        this.resortId = resortId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Integer getFoodOptionId() {
        return foodOptionId;
    }

    public void setFoodOptionId(Integer foodOptionId) {
        this.foodOptionId = foodOptionId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumGuests() {
        return numGuests;
    }

    public void setNumGuests(int numGuests) {
        this.numGuests = numGuests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getResortName() {
        return resortName;
    }

    public void setResortName(String resortName) {
        this.resortName = resortName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(String mealPlan) {
        this.mealPlan = mealPlan;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }
}
