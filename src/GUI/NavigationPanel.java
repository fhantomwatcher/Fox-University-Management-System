package GUI;

import javax.swing.*;
import java.awt.*;
//import java.awt.event.*;
import java.util.*;

public class NavigationPanel extends JPanel {

    private SysGUI parent;
    private Map<String, JButton> buttonMap = new LinkedHashMap<>();

    // Colors
    private final Color normalColor = new Color(41, 53, 65);     // dark gray
    private final Color activeColor = new Color(59, 89, 152);    // blue highlight
    private final Color textColor = Color.WHITE;

    public NavigationPanel(SysGUI parent) {
        this.parent = parent;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(normalColor);
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Add top nav buttons
        addNavButton("Registration");
        addNavButton("Students");
        addNavButton("Teachers");

        // Push the exit button to bottom
        add(Box.createVerticalGlue());

        // Add exit button at the bottom
        addExitButton();
    }

    private void addNavButton(String name) {
        JButton button = new JButton(name);
        button.setFocusPainted(false);
        button.setForeground(textColor);
        button.setBackground(normalColor);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMargin(new Insets(10, 20, 10, 10));

        Dimension buttonSize = new Dimension(180, 40);
        button.setMaximumSize(buttonSize);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);

        button.addActionListener(e -> {
            parent.switchPanel(name);
            highlightButton(name);
        });

        buttonMap.put(name, button);
        add(button);
        add(Box.createVerticalStrut(10));
    }

    private void addExitButton() {
        JButton exitButton = new JButton("Exit");
        exitButton.setFocusPainted(false);
        exitButton.setForeground(textColor);
        exitButton.setBackground(new Color(192, 57, 43)); // Red-ish color
        exitButton.setBorderPainted(false);
        exitButton.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        exitButton.setHorizontalAlignment(SwingConstants.LEFT);
        exitButton.setMargin(new Insets(10, 20, 10, 10));

        Dimension buttonSize = new Dimension(180, 40);
        exitButton.setMaximumSize(buttonSize);
        exitButton.setPreferredSize(buttonSize);
        exitButton.setMinimumSize(buttonSize);

        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                parent,
                "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        add(exitButton);
    }

    public void highlightButton(String name) {
        for (Map.Entry<String, JButton> entry : buttonMap.entrySet()) {
            JButton button = entry.getValue();
            if (entry.getKey().equals(name)) {
                button.setBackground(activeColor);
            } else {
                button.setBackground(normalColor);
            }
        }
    }
}
