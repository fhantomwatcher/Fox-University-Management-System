package GUI;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import GUI.panels.*;

public class SysGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private NavigationPanel navigationPanel; //  Keep reference to highlight on startup

    public SysGUI() {
        setTitle("Fox University Management System");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        //set the windows icon
        URL iconUrl = getClass().getResource("/GUI/Resources/icon/fox.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                setIconImage(icon.getImage());
            } else {
                System.err.println("Icon not found: /GUI/Resources/icon/fox.png");
            }

        initComponents(); // init UI

        // Show dashboard panel & highlight Dashboard button
        switchPanel("RegistrationPanel");
        navigationPanel.highlightButton("Registration");

        setVisible(true);
    }

    private void initComponents() {
        // Sidebar navigation panel
        navigationPanel = new NavigationPanel(this); //store the reference
        add(navigationPanel, BorderLayout.WEST);

        // Main content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Add content panels with keys
        contentPanel.add(new RegistrationPanel(), "Registration");
        contentPanel.add(new StudentPanel(), "Students");
        contentPanel.add(new TeacherPanel(), "Teachers");

        add(contentPanel, BorderLayout.CENTER);
    }

    // Method to switch panels
    public void switchPanel(String name) {
        cardLayout.show(contentPanel, name);
    }
}
