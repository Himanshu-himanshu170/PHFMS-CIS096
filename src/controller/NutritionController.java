package controller;

import database.DatabaseManager;
import model.FoodEntry;

import java.util.List;

/**
 * NutritionController handles all food-related logic:
 * saving entries, calculating daily totals, and querying the database.
 */
public class NutritionController {

    private final DatabaseManager db;

    public NutritionController() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Adds a food entry: looks up nutritional values from the DB,
     * creates a FoodEntry, saves it, and returns it.
     */
    public FoodEntry addFoodEntry(String foodName, double quantityGrams) {
        double[] nutritionValues = db.lookupFood(foodName);
        if (nutritionValues == null) {
            System.err.println("Food not found in database: " + foodName);
            return null;
        }
        FoodEntry entry = new FoodEntry(foodName, quantityGrams, nutritionValues[0], nutritionValues[1]);
        db.saveFoodEntry(entry);
        return entry;
    }

    /** Returns total calories consumed today. */
    public double getTodayTotalCalories() {
        return db.loadTodayFoodLog().stream()
                 .mapToDouble(FoodEntry::calculateTotalCalories)
                 .sum();
    }

    /** Returns total protein consumed today. */
    public double getTodayTotalProtein() {
        return db.loadTodayFoodLog().stream()
                 .mapToDouble(FoodEntry::calculateTotalProtein)
                 .sum();
    }

    /** Returns today's full food log. */
    public List<FoodEntry> getTodayFoodLog() {
        return db.loadTodayFoodLog();
    }

    /** Returns all available food names from the reference table. */
    public List<String> getAvailableFoods() {
        return db.getAllFoodNames();
    }
}
