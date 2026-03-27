package model;

/**
 * FoodEntry represents a food item consumed by the user.
 * Calculates total calories and protein based on quantity consumed.
 */
public class FoodEntry {

    private String foodName;
    private double quantityGrams;
    private double caloriesPerGram;
    private double proteinPerGram;

    public FoodEntry(String foodName, double quantityGrams, double caloriesPerGram, double proteinPerGram) {
        this.foodName        = foodName;
        this.quantityGrams   = quantityGrams;
        this.caloriesPerGram = caloriesPerGram;
        this.proteinPerGram  = proteinPerGram;
    }

    /**
     * Calculates total calories based on quantity consumed.
     * @return total calories (kcal)
     */
    public double calculateTotalCalories() {
        return quantityGrams * caloriesPerGram;
    }

    /**
     * Calculates total protein based on quantity consumed.
     * @return total protein (grams)
     */
    public double calculateTotalProtein() {
        return quantityGrams * proteinPerGram;
    }

    // Getters
    public String getFoodName()         { return foodName; }
    public double getQuantityGrams()    { return quantityGrams; }
    public double getCaloriesPerGram()  { return caloriesPerGram; }
    public double getProteinPerGram()   { return proteinPerGram; }

    @Override
    public String toString() {
        return String.format("FoodEntry[food=%s, qty=%.1fg, calories=%.1fkcal, protein=%.1fg]",
                foodName, quantityGrams, calculateTotalCalories(), calculateTotalProtein());
    }
}
