package controller;

import database.DatabaseManager;
import model.Cardio;
import model.Exercise;
import model.Strength;

import java.util.ArrayList;
import java.util.List;

/**
 * ExerciseController handles exercise logging and calorie burn calculations.
 * Uses polymorphism — both Cardio and Strength are treated as Exercise objects.
 */
public class ExerciseController {

    private final DatabaseManager  db;
    private final List<Exercise>   sessionExercises = new ArrayList<>();

    public ExerciseController() {
        this.db = DatabaseManager.getInstance();
    }

    /**
     * Logs a cardio exercise, saves to database, returns the object.
     */
    public Cardio addCardioExercise(String name, int durationMinutes,
                                    double distanceKm, double userWeightKg) {
        Cardio cardio = new Cardio(name, durationMinutes, distanceKm, userWeightKg);
        sessionExercises.add(cardio);
        db.saveExerciseEntry(name, "Cardio", durationMinutes, cardio.calculateCaloriesBurned());
        return cardio;
    }

    /**
     * Logs a strength exercise, saves to database, returns the object.
     */
    public Strength addStrengthExercise(String name, int durationMinutes,
                                         int sets, int reps, double userWeightKg) {
        Strength strength = new Strength(name, durationMinutes, sets, reps, userWeightKg);
        sessionExercises.add(strength);
        db.saveExerciseEntry(name, "Strength", durationMinutes, strength.calculateCaloriesBurned());
        return strength;
    }

    /**
     * Returns total calories burned across all session exercises.
     * Uses polymorphism — calls calculateCaloriesBurned() on each Exercise.
     */
    public double getTotalCaloriesBurned() {
        return sessionExercises.stream()
                               .mapToDouble(Exercise::calculateCaloriesBurned)
                               .sum();
    }

    public List<Exercise> getSessionExercises() {
        return sessionExercises;
    }
}
