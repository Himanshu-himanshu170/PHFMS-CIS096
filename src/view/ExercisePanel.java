package view;

import controller.AppController;
import model.Exercise;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * ExercisePanel lets the user log cardio or strength exercises.
 */
public class ExercisePanel extends JPanel {

    private final AppController     appController;
    private JComboBox<String>       typeCombo;
    private JTextField              nameField;
    private JTextField              durationField;
    private JTextField              extraField;    // distance (cardio) or sets×reps (strength)
    private JLabel                  extraLabel;
    private DefaultTableModel       tableModel;
    private JLabel                  totalBurnLabel;

    public ExercisePanel(AppController appController) {
        this.appController = appController;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // -- Input panel --
        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Log Exercise"));

        typeCombo     = new JComboBox<>(new String[]{"Cardio", "Strength"});
        nameField     = new JTextField();
        durationField = new JTextField();
        extraLabel    = new JLabel("Distance (km):");
        extraField    = new JTextField();

        typeCombo.addActionListener(e -> {
            if ("Cardio".equals(typeCombo.getSelectedItem())) {
                extraLabel.setText("Distance (km):");
            } else {
                extraLabel.setText("Sets × Reps (e.g. 3x10):");
            }
        });

        inputPanel.add(new JLabel("Type:"));    inputPanel.add(typeCombo);
        inputPanel.add(new JLabel("Name:"));    inputPanel.add(nameField);
        inputPanel.add(new JLabel("Duration (min):")); inputPanel.add(durationField);
        inputPanel.add(extraLabel);             inputPanel.add(extraField);

        JButton logBtn = new JButton("Log Exercise");
        logBtn.addActionListener(e -> logExercise());
        inputPanel.add(new JLabel()); inputPanel.add(logBtn);

        add(inputPanel, BorderLayout.NORTH);

        // -- Table --
        String[] cols = {"Exercise", "Type", "Duration (min)", "Calories Burned"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // -- Total --
        totalBurnLabel = new JLabel("Total burned: 0.0 kcal");
        totalBurnLabel.setFont(totalBurnLabel.getFont().deriveFont(Font.BOLD));
        totalBurnLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        add(totalBurnLabel, BorderLayout.SOUTH);
    }

    private void logExercise() {
        try {
            String type     = (String) typeCombo.getSelectedItem();
            String name     = nameField.getText().trim();
            int    duration = Integer.parseInt(durationField.getText().trim());
            double userWt   = appController.getUserProfile() != null
                              ? appController.getUserProfile().getWeight() : 70.0;

            Exercise exercise;
            if ("Cardio".equals(type)) {
                double dist = Double.parseDouble(extraField.getText().trim());
                exercise = appController.getExerciseController()
                                        .addCardioExercise(name, duration, dist, userWt);
            } else {
                String[] parts = extraField.getText().trim().split("[xX×]");
                int sets = Integer.parseInt(parts[0].trim());
                int reps = Integer.parseInt(parts[1].trim());
                exercise = appController.getExerciseController()
                                        .addStrengthExercise(name, duration, sets, reps, userWt);
            }

            tableModel.addRow(new Object[]{
                exercise.getName(), type, duration,
                String.format("%.1f", exercise.calculateCaloriesBurned())
            });

            totalBurnLabel.setText(String.format("Total burned: %.1f kcal",
                    appController.getExerciseController().getTotalCaloriesBurned()));

            nameField.setText(""); durationField.setText(""); extraField.setText("");
            JOptionPane.showMessageDialog(this,
                exercise.getSummary(), "Exercise Logged", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Please check your inputs.\nFor strength, enter sets×reps (e.g. 3x10).",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
