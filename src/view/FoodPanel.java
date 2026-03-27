package view;

import controller.AppController;
import model.FoodEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * FoodPanel allows the user to log food intake and view today's food log.
 */
public class FoodPanel extends JPanel {

    private final AppController     appController;
    private JComboBox<String>       foodCombo;
    private JTextField              quantityField;
    private DefaultTableModel       tableModel;
    private JLabel                  totalCalLabel;
    private JLabel                  totalProtLabel;

    public FoodPanel(AppController appController) {
        this.appController = appController;
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // -- Input panel (top) --
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        inputPanel.add(new JLabel("Food:"));
        foodCombo     = new JComboBox<>();
        loadFoodOptions();
        inputPanel.add(foodCombo);

        inputPanel.add(new JLabel("Quantity (g):"));
        quantityField = new JTextField(6);
        inputPanel.add(quantityField);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(e -> addFood());
        inputPanel.add(addBtn);

        add(inputPanel, BorderLayout.NORTH);

        // -- Table (centre) --
        String[] cols = {"Food", "Qty (g)", "Calories (kcal)", "Protein (g)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // -- Totals (bottom) --
        JPanel totalsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        totalCalLabel  = new JLabel("Total calories: 0.0 kcal");
        totalProtLabel = new JLabel("Total protein: 0.0 g");
        totalCalLabel.setFont(totalCalLabel.getFont().deriveFont(Font.BOLD));
        totalProtLabel.setFont(totalProtLabel.getFont().deriveFont(Font.BOLD));
        totalsPanel.add(totalCalLabel);
        totalsPanel.add(totalProtLabel);
        add(totalsPanel, BorderLayout.SOUTH);
    }

    private void loadFoodOptions() {
        List<String> foods = appController.getNutritionController().getAvailableFoods();
        for (String f : foods) foodCombo.addItem(f);
    }

    private void addFood() {
        String food = (String) foodCombo.getSelectedItem();
        if (food == null) return;
        try {
            double qty = Double.parseDouble(quantityField.getText().trim());
            FoodEntry entry = appController.getNutritionController().addFoodEntry(food, qty);
            if (entry != null) {
                quantityField.setText("");
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    String.format("Added %s (%.1f kcal)", food, entry.calculateTotalCalories()),
                    "Food Logged", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<FoodEntry> log = appController.getNutritionController().getTodayFoodLog();
        for (FoodEntry e : log) {
            tableModel.addRow(new Object[]{
                e.getFoodName(),
                String.format("%.1f", e.getQuantityGrams()),
                String.format("%.1f", e.calculateTotalCalories()),
                String.format("%.1f", e.calculateTotalProtein())
            });
        }
        totalCalLabel.setText(String.format("Total calories: %.1f kcal",
                appController.getNutritionController().getTodayTotalCalories()));
        totalProtLabel.setText(String.format("Total protein: %.1f g",
                appController.getNutritionController().getTodayTotalProtein()));
    }
}
