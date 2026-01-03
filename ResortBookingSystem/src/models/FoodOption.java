package models;

/**
 * FoodOption model representing cuisine and meal plan options.
 */
public class FoodOption {
    private int id;
    private String cuisineType;
    private String mealPlan;
    private double pricePerDay;

    public FoodOption() {
    }

    public FoodOption(String cuisineType, String mealPlan, double pricePerDay) {
        this.cuisineType = cuisineType;
        this.mealPlan = mealPlan;
        this.pricePerDay = pricePerDay;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    @Override
    public String toString() {
        return cuisineType + " - " + mealPlan + " ($" + String.format("%.2f", pricePerDay) + "/day)";
    }
}
