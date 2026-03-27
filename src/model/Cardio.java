package model;

/**
 * Cardio extends Exercise for cardiovascular activities (running, cycling, etc.).
 * Calorie burn formula: MET value × weight (assumed 70kg default) × duration in hours.
 * Demonstrates inheritance and method overriding (polymorphism).
 */
public class Cardio extends Exercise {

    private double distanceKm;
    private double userWeightKg;

    // MET (Metabolic Equivalent of Task) approximation for moderate cardio
    private static final double MET_VALUE = 7.0;

    public Cardio(String name, int durationMinutes, double distanceKm, double userWeightKg) {
        super(name, durationMinutes);
        this.distanceKm    = distanceKm;
        this.userWeightKg  = userWeightKg;
    }

    /**
     * Estimates calories burned using MET formula:
     * Calories = MET × weight(kg) × duration(hours)
     */
    @Override
    public double calculateCaloriesBurned() {
        double hours = durationMinutes / 60.0;
        return MET_VALUE * userWeightKg * hours;
    }

    public double getDistanceKm()   { return distanceKm; }
    public double getUserWeightKg() { return userWeightKg; }
}
