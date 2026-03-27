import model.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * PHFMSTest contains unit tests for all model classes.
 * Tests verify OOP behaviour: calculation correctness, inheritance, polymorphism.
 *
 * Run with: javac -cp .:junit-4.13.jar PHFMSTest.java && java -cp .:junit-4.13.jar org.junit.runner.JUnitCore PHFMSTest
 */
public class PHFMSTest {

    // ---------------------------------------------------------------
    // UserProfile tests
    // ---------------------------------------------------------------

    @Test
    public void testBMR_Male() {
        // Mifflin-St Jeor: (10×80) + (6.25×175) – (5×30) + 5 = 800 + 1093.75 – 150 + 5 = 1748.75
        UserProfile profile = new UserProfile("Test", 80, 175, 30, "male");
        assertEquals(1748.75, profile.calculateBMR(), 0.01);
    }

    @Test
    public void testBMR_Female() {
        // (10×60) + (6.25×165) – (5×25) – 161 = 600 + 1031.25 – 125 – 161 = 1345.25
        UserProfile profile = new UserProfile("Test", 60, 165, 25, "female");
        assertEquals(1345.25, profile.calculateBMR(), 0.01);
    }

    @Test
    public void testUpdateProfile() {
        UserProfile profile = new UserProfile("Old", 70, 170, 20, "male");
        profile.updateProfile("New", 75, 175, 22, "female");
        assertEquals("New", profile.getName());
        assertEquals(75, profile.getWeight(), 0.01);
        assertEquals("female", profile.getGender());
    }

    // ---------------------------------------------------------------
    // FoodEntry tests
    // ---------------------------------------------------------------

    @Test
    public void testFoodCalorieCalculation() {
        // Chicken breast: 1.65 kcal/g × 200g = 330 kcal
        FoodEntry entry = new FoodEntry("Chicken Breast", 200, 1.65, 0.31);
        assertEquals(330.0, entry.calculateTotalCalories(), 0.01);
    }

    @Test
    public void testFoodProteinCalculation() {
        // 200g × 0.31 = 62g protein
        FoodEntry entry = new FoodEntry("Chicken Breast", 200, 1.65, 0.31);
        assertEquals(62.0, entry.calculateTotalProtein(), 0.01);
    }

    @Test
    public void testFoodEntryZeroQuantity() {
        FoodEntry entry = new FoodEntry("Apple", 0, 0.52, 0.003);
        assertEquals(0.0, entry.calculateTotalCalories(), 0.01);
        assertEquals(0.0, entry.calculateTotalProtein(), 0.01);
    }

    // ---------------------------------------------------------------
    // Cardio tests (inheritance + polymorphism)
    // ---------------------------------------------------------------

    @Test
    public void testCardioCaloriesBurned() {
        // MET=7.0 × 70kg × (30/60)h = 7.0 × 70 × 0.5 = 245 kcal
        Cardio cardio = new Cardio("Running", 30, 5.0, 70.0);
        assertEquals(245.0, cardio.calculateCaloriesBurned(), 0.01);
    }

    @Test
    public void testCardioIsExercise() {
        // Verifies Cardio is a subtype of Exercise (inheritance)
        Cardio cardio = new Cardio("Cycling", 45, 15.0, 75.0);
        assertTrue(cardio instanceof Exercise);
    }

    @Test
    public void testCardioGetSummary() {
        Cardio cardio = new Cardio("Swimming", 60, 2.0, 70.0);
        String summary = cardio.getSummary();
        assertTrue(summary.contains("Swimming"));
        assertTrue(summary.contains("60 min"));
    }

    // ---------------------------------------------------------------
    // Strength tests (inheritance + polymorphism)
    // ---------------------------------------------------------------

    @Test
    public void testStrengthCaloriesBurned() {
        // MET=3.5 × 80kg × (45/60)h = 3.5 × 80 × 0.75 = 210 kcal
        Strength strength = new Strength("Bench Press", 45, 4, 10, 80.0);
        assertEquals(210.0, strength.calculateCaloriesBurned(), 0.01);
    }

    @Test
    public void testStrengthIsExercise() {
        Strength strength = new Strength("Squat", 40, 3, 12, 75.0);
        assertTrue(strength instanceof Exercise);
    }

    @Test
    public void testStrengthSummaryContainsSetsReps() {
        Strength strength = new Strength("Deadlift", 30, 5, 5, 85.0);
        String summary = strength.getSummary();
        assertTrue(summary.contains("5 sets"));
        assertTrue(summary.contains("5 reps"));
    }

    // ---------------------------------------------------------------
    // Polymorphism test — Exercise array contains both subtypes
    // ---------------------------------------------------------------

    @Test
    public void testPolymorphicCaloriesSum() {
        Exercise[] exercises = {
            new Cardio   ("Run",    30, 5.0, 70.0),   // 245 kcal
            new Strength ("Squats", 45, 3,  10, 70.0) // 3.5×70×0.75 = 183.75 kcal
        };
        double total = 0;
        for (Exercise e : exercises) total += e.calculateCaloriesBurned();
        assertEquals(428.75, total, 0.01);
    }
}
