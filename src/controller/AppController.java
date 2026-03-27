package controller;

import database.DatabaseManager;
import model.UserProfile;

/**
 * AppController is the top-level controller.
 * It coordinates the NutritionController and ExerciseController,
 * and manages the user profile.
 */
public class AppController {

    private final DatabaseManager      db;
    private final NutritionController  nutritionCtrl;
    private final ExerciseController   exerciseCtrl;
    private UserProfile                userProfile;

    public AppController() {
        this.db            = DatabaseManager.getInstance();
        this.nutritionCtrl = new NutritionController();
        this.exerciseCtrl  = new ExerciseController();
        this.userProfile   = db.loadUserProfile(); // load saved profile if exists
    }

    /** Saves or updates the user profile. */
    public void saveProfile(String name, double weight, double height, int age, String gender) {
        userProfile = new UserProfile(name, weight, height, age, gender);
        db.saveUserProfile(userProfile);
    }

    /** Returns a formatted daily summary string for the dashboard. */
    public String getDailySummary() {
        double caloriesIn   = nutritionCtrl.getTodayTotalCalories();
        double caloriesOut  = exerciseCtrl.getTotalCaloriesBurned();
        double net          = caloriesIn - caloriesOut;
        double bmr          = (userProfile != null) ? userProfile.calculateBMR() : 0;

        return String.format(
            "Calories consumed : %.1f kcal%n" +
            "Calories burned   : %.1f kcal%n" +
            "Net calories      : %.1f kcal%n" +
            "BMR (daily need)  : %.1f kcal%n" +
            "Protein consumed  : %.1f g",
            caloriesIn, caloriesOut, net, bmr,
            nutritionCtrl.getTodayTotalProtein()
        );
    }

    // Accessors for sub-controllers and profile
    public NutritionController getNutritionController() { return nutritionCtrl; }
    public ExerciseController  getExerciseController()  { return exerciseCtrl; }
    public UserProfile         getUserProfile()         { return userProfile; }
}
