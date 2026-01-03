package models;

/**
 * Resort model representing a resort property.
 */
public class Resort {
    private int id;
    private String name;
    private String location;
    private String description;
    private String imagePath;

    public Resort() {
    }

    public Resort(String name, String location, String description) {
        this.name = name;
        this.location = location;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return name + " - " + location;
    }
}
