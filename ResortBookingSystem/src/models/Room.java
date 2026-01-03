package models;

/**
 * Room model representing a room type within a resort.
 */
public class Room {
    private int id;
    private int resortId;
    private String roomType;
    private int beds;
    private double pricePerNight;
    private int availableCount;

    // Transient field for display purposes
    private String resortName;

    public Room() {
    }

    public Room(int resortId, String roomType, int beds, double pricePerNight, int availableCount) {
        this.resortId = resortId;
        this.roomType = roomType;
        this.beds = beds;
        this.pricePerNight = pricePerNight;
        this.availableCount = availableCount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResortId() {
        return resortId;
    }

    public void setResortId(int resortId) {
        this.resortId = resortId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public String getResortName() {
        return resortName;
    }

    public void setResortName(String resortName) {
        this.resortName = resortName;
    }

    @Override
    public String toString() {
        return roomType + " (" + beds + " beds) - $" + String.format("%.2f", pricePerNight) + "/night";
    }
}
