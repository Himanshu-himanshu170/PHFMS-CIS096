package view;

import controller.AppController;

import javax.swing.*;
import java.awt.*;

/**
 * MainWindow is the root Swing JFrame for the PHFMS application.
 * It hosts all panels in a JTabbedPane following the MVC View role.
 */
public class MainWindow extends JFrame {

    private final AppController appController;
    private JTabbedPane         tabbedPane;

    public MainWindow() {
        this.appController = new AppController();
        initUI();
    }

    private void initUI() {
        setTitle("Personalised Health & Fitness Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 560);
        setLocationRelativeTo(null);
        setResizable(true);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Profile",  new ProfilePanel(appController));
        tabbedPane.addTab("Nutrition", new FoodPanel(appController));
        tabbedPane.addTab("Exercise",  new ExercisePanel(appController));
        tabbedPane.addTab("Dashboard", new DashboardPanel(appController));

        // Refresh dashboard whenever its tab is selected
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3) {
                ((DashboardPanel) tabbedPane.getComponentAt(3)).refresh();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
