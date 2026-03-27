package model;

/**
 * UserProfile stores personal health information for the user.
 * Supports BMR calculation using the Mifflin-St Jeor equation.
 */
public class UserProfile {

    private String name;
    private double weight; // kg
    private double height; // cm
    private int age;
    private String gender; // "male" or "female"

    public UserProfile(String name, double weight, double height, int age, String gender) {
        this.name   = name;
        this.weight = weight;
        this.height = height;
        this.age    = age;
        this.gender = gender;
    }

    /**
     * Calculates Basal Metabolic Rate using the Mifflin-St Jeor equation.
     * @return BMR in kcal/day
     */
    public double calculateBMR() {
        if (gender.equalsIgnoreCase("male")) {
            return (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            return (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }
    }

    public void updateProfile(String name, double weight, double height, int age, String gender) {
        this.name   = name;
        this.weight = weight;
        this.height = height;
        this.age    = age;
        this.gender = gender;
    }

    // Getters
    public String getName()   { return name; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public int    getAge()    { return age; }
    public String getGender() { return gender; }

    @Override
    public String toString() {
        return String.format("UserProfile[name=%s, weight=%.1fkg, height=%.1fcm, age=%d, gender=%s, BMR=%.1f]",
                name, weight, height, age, gender, calculateBMR());
    }
}
