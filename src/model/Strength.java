package model;

/**
 * Strength extends Exercise for resistance/weight training activities.
 * Uses a lower MET value than cardio and factors in sets and reps.
 * Demonstrates inheritance and method overriding (polymorphism).
 */
public class Strength extends Exercise {

    private int    sets;
    private int    reps;
    private double userWeightKg;

    // MET value for moderate strength training
    private static final double MET_VALUE = 3.5;

    public Strength(String name, int durationMinutes, int sets, int reps, double userWeightKg) {
        super(name, durationMinutes);
        this.sets          = sets;
        this.reps          = reps;
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

    @Override
    public String getSummary() {
        return String.format("%s — %d sets × %d reps — %d min — %.1f kcal burned",
                name, sets, reps, durationMinutes, calculateCaloriesBurned());
    }

    public int    getSets()         { return sets; }
    public int    getReps()         { return reps; }
    public double getUserWeightKg() { return userWeightKg; }
}
