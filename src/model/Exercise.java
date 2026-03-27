package model;

/**
 * Exercise is an abstract base class representing a physical activity.
 * Subclasses (Cardio, Strength) override calculateCaloriesBurned().
 * Demonstrates inheritance and polymorphism (OOP principles).
 */
public abstract class Exercise {

    protected String name;
    protected int    durationMinutes;

    public Exercise(String name, int durationMinutes) {
        this.name            = name;
        this.durationMinutes = durationMinutes;
    }

    /**
     * Abstract method: each subclass calculates calories burned differently.
     * @return estimated calories burned (kcal)
     */
    public abstract double calculateCaloriesBurned();

    /**
     * Returns a summary string of this exercise entry.
     */
    public String getSummary() {
        return String.format("%s — %d min — %.1f kcal burned",
                name, durationMinutes, calculateCaloriesBurned());
    }

    // Getters
    public String getName()           { return name; }
    public int    getDurationMinutes(){ return durationMinutes; }
}
