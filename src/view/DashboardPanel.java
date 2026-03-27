package view;

import controller.AppController;
import model.UserProfile;

import javax.swing.*;
import java.awt.*;

/**
 * DashboardPanel displays a daily summary of calories consumed vs burned.
 * Refreshed every time the user navigates to this tab.
 */
public class DashboardPanel extends JPanel {

    private final AppController appController;
    private JTextArea           summaryArea;
    private JProgressBar        calProgressBar;
    private JLabel              statusLabel;

    public DashboardPanel(AppController appController) {
        this.appController = appController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Daily Summary", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        summaryArea = new JTextArea(10, 40);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(summaryArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(6, 6));
        calProgressBar = new JProgressBar(0, 100);
        calProgressBar.setStringPainted(true);
        calProgressBar.setString("Net calories vs BMR");
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        bottomPanel.add(new JLabel("Calorie balance:"), BorderLayout.WEST);
        bottomPanel.add(calProgressBar, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /** Called whenever the dashboard tab is shown. */
    public void refresh() {
        summaryArea.setText(appController.getDailySummary());

        UserProfile profile = appController.getUserProfile();
        if (profile != null) {
            double bmr         = profile.calculateBMR();
            double caloriesIn  = appController.getNutritionController().getTodayTotalCalories();
            double caloriesOut = appController.getExerciseController().getTotalCaloriesBurned();
            double net         = caloriesIn - caloriesOut;

            int progress = (int) Math.min(100, (net / bmr) * 100);
            calProgressBar.setValue(Math.max(0, progress));
            calProgressBar.setString(String.format("%.0f / %.0f kcal (%.0f%%)", net, bmr, (net / bmr) * 100));

            if (net < bmr * 0.9) {
                statusLabel.setText("Under target — consider eating more.");
                statusLabel.setForeground(Color.BLUE);
            } else if (net > bmr * 1.1) {
                statusLabel.setText("Over target — consider more exercise.");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("On track — good balance today!");
                statusLabel.setForeground(new Color(0, 140, 0));
            }
        }
    }
}
