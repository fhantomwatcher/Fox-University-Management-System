package GUI.panels;

import GUI.Entity.Student;
import GUI.Entity.Teacher;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class RegistrationPanel extends JPanel {
    private JPanel registrationCards; // CardLayout panel to switch forms
    private CardLayout cardLayout;
    private JLabel photoLabel;
    private String selectedPhotoPath = "";
    
    // Student form fields
    private JTextField studentNameField;
    private JTextField studentAgeField;
    private JTextField studentEmailField;
    private JComboBox<String> studentDepartmentCombo;
    private ButtonGroup studentGenderGroup;
    private JRadioButton studentMaleRadio, studentFemaleRadio, studentOtherRadio;
    private JTextArea studentAddressArea;
    
    // Teacher form fields
    private JTextField teacherNameField;
    private JTextField teacherAgeField;
    private JTextField teacherEmailField;
    private JComboBox<String> teacherDepartmentCombo;
    private JComboBox<String> teacherSubjectCombo;
    private JComboBox<String> teacherDesignationCombo;
    private ButtonGroup teacherGenderGroup;
    private JRadioButton teacherMaleRadio, teacherFemaleRadio, teacherOtherRadio;
    private JTextArea teacherAddressArea;
    private JLabel teacherPhotoLabel;
    private String teacherPhotoPath = "";

    // private static final int PHOTO_WIDTH = 225;  // Portrait 16:9
    // private static final int PHOTO_HEIGHT = 400; // Portrait 16:9

    public RegistrationPanel() {
        setLayout(new BorderLayout());

        // Main panel with vertical BoxLayout to center all components vertically
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // unified padding

        // Welcome label at top, centered horizontally
        JLabel welcomeLabel = new JLabel("Registration Form", SwingConstants.CENTER);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 102, 204));
        mainPanel.add(welcomeLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // vertical spacing

        // Dropdown to select registration type
        String[] registrationTypes = {"Student", "Teacher"};
        JComboBox<String> registrationSelector = new JComboBox<>(registrationTypes);
        registrationSelector.setMaximumSize(new Dimension(200, 35));
        registrationSelector.setAlignmentX(Component.CENTER_ALIGNMENT);
        registrationSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        // Label for dropdown, centered
        JLabel selectorLabel = new JLabel("Select Registration Type:");
        selectorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectorLabel.setFont(new Font("Arial", Font.BOLD, 16));

        mainPanel.add(selectorLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        mainPanel.add(registrationSelector);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // CardLayout panel to switch registration forms
        cardLayout = new CardLayout();
        registrationCards = new JPanel(cardLayout);
        registrationCards.setAlignmentX(Component.CENTER_ALIGNMENT);
        registrationCards.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "Registration Details",
            0, 0, new Font("Arial", Font.BOLD, 14), new Color(0, 102, 204)
        ));

        // Add student and teacher registration panels
        JScrollPane studentScroll = new JScrollPane(createStudentRegistrationPanel(),
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        studentScroll.setPreferredSize(new Dimension(500, 600));
        registrationCards.add(studentScroll, "Student");

        JScrollPane teacherScroll = new JScrollPane(createTeacherRegistrationPanel(),
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        teacherScroll.setPreferredSize(new Dimension(500, 700));
        registrationCards.add(teacherScroll, "Teacher");

        // Show student registration by default
        cardLayout.show(registrationCards, "Student");

        mainPanel.add(registrationCards);

        add(mainPanel, BorderLayout.CENTER);

        // Switch cards when dropdown selection changes
        registrationSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) registrationSelector.getSelectedItem();
                cardLayout.show(registrationCards, selected);
            }
        });
    }

    // Utility: Move focus to next component on Enter key
    private void addEnterKeyTraversal(JComponent component, JComponent next) {
        component.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "moveFocus");
        component.getActionMap().put("moveFocus", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (next != null) next.requestFocusInWindow();
            }
        });
    }

    private JPanel createStudentRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        studentNameField = new JTextField(20);
        panel.add(studentNameField, gbc);

        // Age field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        studentAgeField = new JTextField(20);
        panel.add(studentAgeField, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        studentEmailField = new JTextField(20);
        panel.add(studentEmailField, gbc);

        // Department dropdown
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        String[] departments = {"Computer Science", "Engineering", "Mathematics", "Physics", "Chemistry", "Biology", "Business", "Literature"};
        studentDepartmentCombo = new JComboBox<>(departments);
        panel.add(studentDepartmentCombo, gbc);

        // Gender radio buttons
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentGenderGroup = new ButtonGroup();
        studentMaleRadio = new JRadioButton("Male");
        studentFemaleRadio = new JRadioButton("Female");
        studentOtherRadio = new JRadioButton("Other");
        studentGenderGroup.add(studentMaleRadio);
        studentGenderGroup.add(studentFemaleRadio);
        studentGenderGroup.add(studentOtherRadio);
        genderPanel.add(studentMaleRadio);
        genderPanel.add(studentFemaleRadio);
        genderPanel.add(studentOtherRadio);
        genderPanel.setOpaque(false);
        panel.add(genderPanel, gbc);

        // Address text area
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        studentAddressArea = new JTextArea(4, 20);
        studentAddressArea.setLineWrap(true);
        studentAddressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(studentAddressArea);
        panel.add(addressScroll, gbc);

        // Photo upload section
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(new JLabel("Photo:"), gbc);
        gbc.gridx = 1;
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoLabel = new JLabel("No photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(80, 100));
        photoLabel.setMinimumSize(new Dimension(80, 100));
        photoLabel.setMaximumSize(new Dimension(80, 100));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JButton photoButton = new JButton("Choose Photo");
        photoButton.addActionListener(e -> selectPhoto());
        photoPanel.add(photoLabel, BorderLayout.CENTER);
        photoPanel.add(photoButton, BorderLayout.SOUTH);
        photoPanel.setOpaque(false);
        panel.add(photoPanel, gbc);

        // Register button
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 10, 15);
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.addActionListener(e -> submitStudentRegistration());
        panel.add(registerButton, gbc);

        // --- Keyboard navigation setup ---
        addEnterKeyTraversal(studentNameField, studentAgeField);
        addEnterKeyTraversal(studentAgeField, studentEmailField);
        addEnterKeyTraversal(studentEmailField, studentDepartmentCombo);
        addEnterKeyTraversal(studentDepartmentCombo, studentMaleRadio);
        addEnterKeyTraversal(studentMaleRadio, studentFemaleRadio);
        addEnterKeyTraversal(studentFemaleRadio, studentOtherRadio);
        addEnterKeyTraversal(studentOtherRadio, studentAddressArea);
        addEnterKeyTraversal(studentAddressArea, photoLabel);
        addEnterKeyTraversal(photoLabel, registerButton);
        // Enter on registerButton submits the form
        registerButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "press");
        registerButton.getActionMap().put("press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitStudentRegistration();
            }
        });

        return panel;
    }

    private JPanel createTeacherRegistrationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        teacherNameField = new JTextField(20);
        panel.add(teacherNameField, gbc);

        // Age field
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        teacherAgeField = new JTextField(20);
        panel.add(teacherAgeField, gbc);

        // Email field
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        teacherEmailField = new JTextField(20);
        panel.add(teacherEmailField, gbc);

        // Department dropdown
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        String[] departments = {"Computer Science", "Engineering", "Mathematics", "Physics", "Chemistry", "Biology", "Business", "Literature"};
        teacherDepartmentCombo = new JComboBox<>(departments);
        panel.add(teacherDepartmentCombo, gbc);

        // Subject dropdown
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Subject:"), gbc);
        gbc.gridx = 1;
        String[] subjects = {"Data Structures", "Algorithms", "Database Systems", "Software Engineering", "Web Development", "Machine Learning", "Calculus", "Linear Algebra", "Physics I", "Chemistry I", "Biology I", "Business Management"};
        teacherSubjectCombo = new JComboBox<>(subjects);
        panel.add(teacherSubjectCombo, gbc);

        // Designation dropdown
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Designation:"), gbc);
        gbc.gridx = 1;
        String[] designations = {"Lecturer", "Assistant Professor", "Associate Professor", "Professor", "Department Head", "Dean"};
        teacherDesignationCombo = new JComboBox<>(designations);
        panel.add(teacherDesignationCombo, gbc);

        // Gender radio buttons
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        teacherGenderGroup = new ButtonGroup();
        teacherMaleRadio = new JRadioButton("Male");
        teacherFemaleRadio = new JRadioButton("Female");
        teacherOtherRadio = new JRadioButton("Other");
        teacherGenderGroup.add(teacherMaleRadio);
        teacherGenderGroup.add(teacherFemaleRadio);
        teacherGenderGroup.add(teacherOtherRadio);
        genderPanel.add(teacherMaleRadio);
        genderPanel.add(teacherFemaleRadio);
        genderPanel.add(teacherOtherRadio);
        genderPanel.setOpaque(false);
        panel.add(genderPanel, gbc);

        // Address text area
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        teacherAddressArea = new JTextArea(4, 20);
        teacherAddressArea.setLineWrap(true);
        teacherAddressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(teacherAddressArea);
        panel.add(addressScroll, gbc);

        // Photo upload section
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(new JLabel("Photo:"), gbc);
        gbc.gridx = 1;
        JPanel photoPanel = new JPanel(new BorderLayout());
        teacherPhotoLabel = new JLabel("No photo selected", SwingConstants.CENTER);
        teacherPhotoLabel.setPreferredSize(new Dimension(80, 100));
        teacherPhotoLabel.setMinimumSize(new Dimension(80, 100));
        teacherPhotoLabel.setMaximumSize(new Dimension(80, 100));
        teacherPhotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        teacherPhotoLabel.setVerticalAlignment(SwingConstants.CENTER);
        teacherPhotoLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JButton photoButton = new JButton("Choose Photo");
        photoButton.addActionListener(e -> selectTeacherPhoto(teacherPhotoLabel));
        photoPanel.add(teacherPhotoLabel, BorderLayout.CENTER);
        photoPanel.add(photoButton, BorderLayout.SOUTH);
        photoPanel.setOpaque(false);
        panel.add(photoPanel, gbc);

        // Register button
        gbc.gridx = 0; gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 10, 15);
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.addActionListener(e -> submitTeacherRegistration());
        panel.add(registerButton, gbc);

        // --- Keyboard navigation setup ---
        addEnterKeyTraversal(teacherNameField, teacherAgeField);
        addEnterKeyTraversal(teacherAgeField, teacherEmailField);
        addEnterKeyTraversal(teacherEmailField, teacherDepartmentCombo);
        addEnterKeyTraversal(teacherDepartmentCombo, teacherSubjectCombo);
        addEnterKeyTraversal(teacherSubjectCombo, teacherDesignationCombo);
        addEnterKeyTraversal(teacherDesignationCombo, teacherMaleRadio);
        addEnterKeyTraversal(teacherMaleRadio, teacherFemaleRadio);
        addEnterKeyTraversal(teacherFemaleRadio, teacherOtherRadio);
        addEnterKeyTraversal(teacherOtherRadio, teacherAddressArea);
        addEnterKeyTraversal(teacherAddressArea, teacherPhotoLabel);
        addEnterKeyTraversal(teacherPhotoLabel, registerButton);
        // Enter on registerButton submits the form
        registerButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "press");
        registerButton.getActionMap().put("press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitTeacherRegistration();
            }
        });

        return panel;
    }

    private void selectPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedPhotoPath = selectedFile.getAbsolutePath();
            photoLabel.setText(""); // Clear text

            try {
                ImageIcon imageIcon = new ImageIcon(selectedPhotoPath);
                Image img = imageIcon.getImage();
                // Always scale to exactly 80x100, do not let the label grow
                Image scaledImg = img.getScaledInstance(80, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImg));
                photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                photoLabel.setVerticalAlignment(SwingConstants.CENTER);
            } catch (Exception e) {
                photoLabel.setText("Photo: " + selectedFile.getName());
                photoLabel.setIcon(null);
            }
        }
    }

    private void selectTeacherPhoto(JLabel photoLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            teacherPhotoPath = selectedFile.getAbsolutePath();
            photoLabel.setText(""); // Clear text

            try {
                ImageIcon imageIcon = new ImageIcon(teacherPhotoPath);
                Image img = imageIcon.getImage();
                // Always scale to exactly 80x100, do not let the label grow
                Image scaledImg = img.getScaledInstance(80, 100, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImg));
                photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                photoLabel.setVerticalAlignment(SwingConstants.CENTER);
            } catch (Exception e) {
                photoLabel.setText("Photo: " + selectedFile.getName());
                photoLabel.setIcon(null);
            }
        }
    }

    // Validation methods
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    private boolean isValidAge(String age) {
        try {
            int ageValue = Integer.parseInt(age);
            return ageValue >= 10 && ageValue <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidName(String name) {
        return name != null && name.trim().length() >= 1 && name.matches("^[a-zA-Z\\s]+$");
    }

    private String getSelectedGender(ButtonGroup genderGroup) {
        if (genderGroup.getSelection() != null) {
            for (AbstractButton button : java.util.Collections.list(genderGroup.getElements())) {
                if (button.isSelected()) {
                    return button.getText();
                }
            }
        }
        return null;
    }

    private boolean validateStudentForm() {
        StringBuilder errors = new StringBuilder();

        // Validate name
        String name = studentNameField.getText().trim();
        if (!isValidName(name)) {
            errors.append("• Name must be at least 1 characters and contain only letters\n");
        }

        // Validate age
        String age = studentAgeField.getText().trim();
        if (!isValidAge(age)) {
            errors.append("• Age must be a number between 10 and 100\n");
        }

        // Validate email
        String email = studentEmailField.getText().trim();
        if (!isValidEmail(email)) {
            errors.append("• Please enter a valid email address\n");
        }

        // Validate gender selection
        if (getSelectedGender(studentGenderGroup) == null) {
            errors.append("• Please select a gender\n");
        }

        // Validate address
        String address = studentAddressArea.getText().trim();
        if (address.length() < 5) {
            errors.append("• Address must be at least 10 characters long\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Please fill the sections as directed:\n\n" + errors.toString(), 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validateTeacherForm() {
        StringBuilder errors = new StringBuilder();

        // Validate name
        String name = teacherNameField.getText().trim();
        if (!isValidName(name)) {
            errors.append("• Name must be at least 1 characters and contain only letters\n");
        }

        // Validate age
        String age = teacherAgeField.getText().trim();
        if (!isValidAge(age)) {
            errors.append("• Age must be a number between 10 and 100\n");
        }

        // Validate email
        String email = teacherEmailField.getText().trim();
        if (!isValidEmail(email)) {
            errors.append("• Please enter a valid email address\n");
        }

        // Validate gender selection
        if (getSelectedGender(teacherGenderGroup) == null) {
            errors.append("• Please select a gender\n");
        }

        // Validate address
        String address = teacherAddressArea.getText().trim();
        if (address.length() < 5) {
            errors.append("• Address must be at least 10 characters long\n");
        }

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, 
                "Please fill the sections as directed:\n\n" + errors.toString(), 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void submitStudentRegistration() {
        if (!validateStudentForm()) {
            return;
        }

        try {
            // Collect student data
            String name = studentNameField.getText().trim();
            String age = studentAgeField.getText().trim();
            String email = studentEmailField.getText().trim();
            String department = (String) studentDepartmentCombo.getSelectedItem();
            String gender = getSelectedGender(studentGenderGroup);
            String address = studentAddressArea.getText().trim();
            String photoPath = selectedPhotoPath;

            // Create student entity and save to CSV
            Student student = new Student(name, age, email, department, gender, address, photoPath);
            student.saveToCSV();

            // Display success message with registration details
            String message = String.format(
                "Student Registration Successful!\n\n" +
                "Student ID: %s\n" +
                "Name: %s\n" +
                "Age: %s\n" +
                "Email: %s\n" +
                "Department: %s\n" +
                "Gender: %s\n" +
                "Address: %s\n" +
                "Photo: %s\n" +
                "Registration Date: %s\n\n" +
                "Data saved to CSV file successfully!",
                student.getId(), name, age, email, department, gender, address,
                (photoPath.isEmpty() ? "Not provided" : "Uploaded"),
                student.getRegistrationDate()
            );

            JOptionPane.showMessageDialog(this, message, "Student Registration Successful", JOptionPane.INFORMATION_MESSAGE);

            // Clear form after successful registration
            clearStudentForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving student data: " + e.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitTeacherRegistration() {
        if (!validateTeacherForm()) {
            return;
        }

        try {
            // Collect teacher data
            String name = teacherNameField.getText().trim();
            String age = teacherAgeField.getText().trim();
            String email = teacherEmailField.getText().trim();
            String department = (String) teacherDepartmentCombo.getSelectedItem();
            String subject = (String) teacherSubjectCombo.getSelectedItem();
            String designation = (String) teacherDesignationCombo.getSelectedItem();
            String gender = getSelectedGender(teacherGenderGroup);
            String address = teacherAddressArea.getText().trim();
            String photoPath = teacherPhotoPath;

            // Create teacher entity and save to CSV
            Teacher teacher = new Teacher(name, age, email, department, subject, designation, gender, address, photoPath);
            teacher.saveToCSV();

            // Display success message with registration details
            String message = String.format(
                "Teacher Registration Successful!\n\n" +
                "Employee ID: %s\n" +
                "Name: %s\n" +
                "Age: %s\n" +
                "Email: %s\n" +
                "Department: %s\n" +
                "Subject: %s\n" +
                "Designation: %s\n" +
                "Gender: %s\n" +
                "Address: %s\n" +
                "Photo: %s\n" +
                "Registration Date: %s\n\n" +
                "Data saved to CSV file successfully!",
                teacher.getId(), name, age, email, department, subject, designation, gender, address,
                (photoPath.isEmpty() ? "Not provided" : "Uploaded"),
                teacher.getRegistrationDate()
            );

            JOptionPane.showMessageDialog(this, message, "Teacher Registration Successful", JOptionPane.INFORMATION_MESSAGE);

            // Clear form after successful registration
            clearTeacherForm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving teacher data: " + e.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearStudentForm() {
        studentNameField.setText("");
        studentAgeField.setText("");
        studentEmailField.setText("");
        studentDepartmentCombo.setSelectedIndex(0);
        studentGenderGroup.clearSelection();
        studentAddressArea.setText("");
        photoLabel.setIcon(null);
        photoLabel.setText("No photo selected");
        selectedPhotoPath = "";
    }

    private void clearTeacherForm() {
        teacherNameField.setText("");
        teacherAgeField.setText("");
        teacherEmailField.setText("");
        teacherDepartmentCombo.setSelectedIndex(0);
        teacherSubjectCombo.setSelectedIndex(0);
        teacherDesignationCombo.setSelectedIndex(0);
        teacherGenderGroup.clearSelection();
        teacherAddressArea.setText("");
        teacherPhotoLabel.setIcon(null);
        teacherPhotoLabel.setText("No photo selected");
        teacherPhotoPath = "";
    }
}