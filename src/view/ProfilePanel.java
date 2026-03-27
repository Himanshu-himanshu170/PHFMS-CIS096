package view;

import controller.AppController;
import model.UserProfile;

import javax.swing.*;
import java.awt.*;

/**
 * ProfilePanel allows the user to enter and save their personal health profile.
 */
public class ProfilePanel extends JPanel {

    private final AppController appController;

    private JTextField nameField;
    private JTextField weightField;
    private JTextField heightField;
    private JTextField ageField;
    private JComboBox<String> genderBox;
    private JLabel    bmrLabel;

    public ProfilePanel(AppController appController) {
        this.appController = appController;
        initUI();
        loadExistingProfile();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.anchor  = GridBagConstraints.WEST;

        nameField   = new JTextField(20);
        weightField = new JTextField(20);
        heightField = new JTextField(20);
        ageField    = new JTextField(20);
        genderBox   = new JComboBox<>(new String[]{"Male", "Female"});
        bmrLabel    = new JLabel("BMR: —");
        bmrLabel.setFont(bmrLabel.getFont().deriveFont(Font.BOLD));

        String[] labels = {"Name:", "Weight (kg):", "Height (cm):", "Age:", "Gender:"};
        JComponent[] fields = {nameField, weightField, heightField, ageField, genderBox};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            add(fields[i], gbc);
        }

        JButton saveBtn = new JButton("Save Profile");
        saveBtn.addActionListener(e -> saveProfile());

        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(saveBtn, gbc);

        gbc.gridy = labels.length + 1;
        add(bmrLabel, gbc);
    }

    private void saveProfile() {
        try {
            String name   = nameField.getText().trim();
            double weight = Double.parseDouble(weightField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            int    age    = Integer.parseInt(ageField.getText().trim());
            String gender = (String) genderBox.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your name.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            appController.saveProfile(name, weight, height, age, gender.toLowerCase());
            double bmr = appController.getUserProfile().calculateBMR();
            bmrLabel.setText(String.format("BMR: %.1f kcal/day", bmr));
            JOptionPane.showMessageDialog(this, "Profile saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for weight, height, and age.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadExistingProfile() {
        UserProfile profile = appController.getUserProfile();
        if (profile != null) {
            nameField.setText(profile.getName());
            weightField.setText(String.valueOf(profile.getWeight()));
            heightField.setText(String.valueOf(profile.getHeight()));
            ageField.setText(String.valueOf(profile.getAge()));
            genderBox.setSelectedItem(profile.getGender().substring(0, 1).toUpperCase()
                                      + profile.getGender().substring(1));
            bmrLabel.setText(String.format("BMR: %.1f kcal/day", profile.calculateBMR()));
        }
    }
}
