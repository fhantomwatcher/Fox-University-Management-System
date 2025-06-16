package GUI.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class TeacherPanel extends JPanel {
    private Map<String, List<Teacher>> departmentTeachers = new HashMap<>();
    private JComboBox<String> departmentFilter;
    private JTextField searchField;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private String csvFilePath = "src/userdata/teachers.csv";
    private static final int PHOTO_WIDTH = 80;
    private static final int PHOTO_HEIGHT = 100;

    public TeacherPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadTeachersFromCSV();
        setupLayout();
        departmentFilter.addActionListener(e -> filterByDepartment());
        displayTeachers("All");
    }

    private void initializeComponents() {
        departmentFilter = new JComboBox<>();
        departmentFilter.addItem("All");

        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchTeachers());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245));
    }

    private void setupLayout() {
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        contentPanel.addMouseWheelListener(e -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            int amount = e.getUnitsToScroll() * bar.getUnitIncrement();
            bar.setValue(bar.getValue() + amount);
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(70, 130, 180));

        JLabel titleLabel = new JLabel("Faculty List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setOpaque(false);

        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setForeground(Color.WHITE);
        deptLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filterPanel.add(deptLabel);
        filterPanel.add(departmentFilter);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(Color.WHITE);
        searchLabel.setFont(new Font("Arial", Font.BOLD, 12));
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchTeachers());
        filterPanel.add(searchBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshData());
        filterPanel.add(refreshBtn);

        headerPanel.add(filterPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    // Robust CSV parser for quoted fields (handles commas in address)
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().replace("\"\"", "\""));
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().replace("\"\"", "\""));
        return result;
    }

    private void loadTeachersFromCSV() {
        departmentTeachers.clear();

        ActionListener[] listeners = departmentFilter.getActionListeners();
        for (ActionListener listener : listeners) {
            departmentFilter.removeActionListener(listener);
        }

        departmentFilter.removeAllItems();
        departmentFilter.addItem("All");

        File csvFile = new File(csvFilePath);
        if (!csvFile.exists()) {
            JOptionPane.showMessageDialog(this, "No teacher data found. Please register teachers first.");
            for (ActionListener listener : listeners) {
                departmentFilter.addActionListener(listener);
            }
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }

                List<String> data = parseCSVLine(line);
                if (data.size() >= 10) {
                    String id = data.get(0).trim();
                    String name = data.get(1).trim();
                    String age = data.get(2).trim();
                    String email = data.get(3).trim();
                    String department = data.get(4).trim();
                    String subject = data.get(5).trim();
                    String designation = data.get(6).trim();
                    String gender = data.get(7).trim();
                    String address = data.get(8).trim();
                    String photoPath = data.get(9).trim().replace("\\", "/");
                    String registrationDate = data.size() > 10 ? data.get(10).trim() : "";

                    Teacher teacher = new Teacher(
                        id, name, age, email, department, subject, designation, gender, address, photoPath, registrationDate
                    );

                    departmentTeachers.computeIfAbsent(department, k -> new ArrayList<>()).add(teacher);

                    // Add department to filter if not present
                    boolean deptExists = false;
                    for (int i = 0; i < departmentFilter.getItemCount(); i++) {
                        if (departmentFilter.getItemAt(i).equals(department)) {
                            deptExists = true;
                            break;
                        }
                    }
                    if (!deptExists && !department.isEmpty()) {
                        departmentFilter.addItem(department);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading teacher data: " + e.getMessage());
        }

        for (ActionListener listener : listeners) {
            departmentFilter.addActionListener(listener);
        }
    }

    private void displayTeachers(String selectedDepartment) {
        if (selectedDepartment == null) selectedDepartment = "All";
        contentPanel.removeAll();

        if (selectedDepartment.equals("All")) {
            List<String> sortedDepts = new ArrayList<>(departmentTeachers.keySet());
            Collections.sort(sortedDepts);
            for (String dept : sortedDepts) {
                addDepartmentSection(dept, departmentTeachers.get(dept));
            }
        } else {
            List<Teacher> teachers = departmentTeachers.get(selectedDepartment);
            if (teachers != null) {
                addDepartmentSection(selectedDepartment, teachers);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addDepartmentSection(String department, List<Teacher> teachers) {
        JPanel deptHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deptHeaderPanel.setBackground(new Color(245, 245, 245));
        deptHeaderPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel deptLabel = new JLabel(department + " (" + teachers.size() + " teachers)");
        deptLabel.setFont(new Font("Arial", Font.BOLD, 18));
        deptLabel.setForeground(new Color(70, 130, 180));
        deptHeaderPanel.add(deptLabel);

        contentPanel.add(deptHeaderPanel);

        // Responsive grid: 2 columns for wide screens, 1 for narrow
        int columns = Math.max(1, contentPanel.getWidth() / 400);
        if (columns < 2) columns = 2;

        JPanel teachersGrid = new JPanel(new GridBagLayout());
        teachersGrid.setBackground(new Color(245, 245, 245));
        teachersGrid.setBorder(new EmptyBorder(0, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        for (int i = 0; i < teachers.size(); i++) {
            Teacher teacher = teachers.get(i);
            gbc.gridx = i % columns;
            gbc.gridy = i / columns;
            teachersGrid.add(createTeacherCard(teacher), gbc);
        }

        contentPanel.add(teachersGrid);
    }

    private JPanel createTeacherCard(Teacher teacher) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        // Photo on the left
        JLabel photoLabel = createPhotoLabel(teacher.getPhotoPath());
        photoLabel.setPreferredSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setMinimumSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setMaximumSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Details on the right
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(teacher.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(70, 130, 180));

        JLabel idLabel = new JLabel("ID: " + teacher.getTeacherId());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        idLabel.setForeground(Color.GRAY);

        JLabel deptLabel = new JLabel("Dept: " + teacher.getDepartment());
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel subjectLabel = new JLabel("Subject: " + teacher.getSubject());
        subjectLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel designationLabel = new JLabel("Designation: " + teacher.getDesignation());
        designationLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel ageGenderLabel = new JLabel("Age: " + teacher.getAge() + " | " + teacher.getGender());
        ageGenderLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel emailLabel = new JLabel("Email: " + teacher.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel addressLabel = new JLabel("<html><body style='width:220px'>Address: " + teacher.getAddress() + "</body></html>");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        addressLabel.setForeground(new Color(60, 60, 60));

        JLabel regLabel = new JLabel("Registered: " + teacher.getRegistrationDate());
        regLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        regLabel.setForeground(new Color(120, 120, 120));

        infoPanel.add(nameLabel);
        infoPanel.add(idLabel);
        infoPanel.add(deptLabel);
        infoPanel.add(subjectLabel);
        infoPanel.add(designationLabel);
        infoPanel.add(ageGenderLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(addressLabel);
        infoPanel.add(regLabel);

        card.add(photoLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(240, 248, 255));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                        new EmptyBorder(7, 7, 7, 7)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        new EmptyBorder(8, 8, 8, 8)
                ));
            }
        });

        return card;
    }

    private JLabel createPhotoLabel(String photoPath) {
        JLabel photoLabel = new JLabel();
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);

        try {
            String normalizedPath = photoPath.replace("\"", "").replace("\\", "/").trim();
            File photoFile = new File(normalizedPath);
            if (!photoFile.isAbsolute()) {
                String projectRoot = System.getProperty("user.dir");
                photoFile = new File(projectRoot, normalizedPath);
            }
            if (photoFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(photoFile.getAbsolutePath());
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(PHOTO_WIDTH, PHOTO_HEIGHT, Image.SCALE_SMOOTH);
                photoLabel.setIcon(new ImageIcon(scaledImg));
                photoLabel.setText("");
            } else {
                photoLabel.setText("👤");
                photoLabel.setFont(new Font("Arial", Font.PLAIN, 28));
                photoLabel.setForeground(new Color(70, 130, 180));
                photoLabel.setOpaque(true);
                photoLabel.setBackground(new Color(240, 248, 255));
            }
        } catch (Exception e) {
            photoLabel.setText("👤");
            photoLabel.setFont(new Font("Arial", Font.PLAIN, 28));
            photoLabel.setForeground(new Color(70, 130, 180));
            photoLabel.setOpaque(true);
            photoLabel.setBackground(new Color(240, 248, 255));
        }

        return photoLabel;
    }

    private void filterByDepartment() {
        String selected = (String) departmentFilter.getSelectedItem();
        if (selected == null) selected = "All";
        displayTeachers(selected);
    }

    private void searchTeachers() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            displayTeachers((String) departmentFilter.getSelectedItem());
            return;
        }

        contentPanel.removeAll();

        for (String dept : departmentTeachers.keySet()) {
            List<Teacher> filteredTeachers = new ArrayList<>();
            for (Teacher teacher : departmentTeachers.get(dept)) {
                if (
                        teacher.getName().toLowerCase().contains(searchTerm) ||
                        teacher.getTeacherId().toLowerCase().contains(searchTerm) ||
                        teacher.getEmail().toLowerCase().contains(searchTerm) ||
                        teacher.getDepartment().toLowerCase().contains(searchTerm) ||
                        teacher.getSubject().toLowerCase().contains(searchTerm) ||
                        teacher.getDesignation().toLowerCase().contains(searchTerm) ||
                        teacher.getGender().toLowerCase().contains(searchTerm) ||
                        teacher.getAddress().toLowerCase().contains(searchTerm)
                ) {
                    filteredTeachers.add(teacher);
                }
            }
            if (!filteredTeachers.isEmpty()) {
                addDepartmentSection(dept, filteredTeachers);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void refreshData() {
        loadTeachersFromCSV();
        displayTeachers("All");
        searchField.setText("");
        if (departmentFilter.getItemCount() > 0) {
            departmentFilter.setSelectedItem("All");
        }
    }

    // Teacher class for your CSV structure
    private static class Teacher {
        private String teacherId, name, age, email, department, subject, designation, gender, address, photoPath, registrationDate;

        public Teacher(String teacherId, String name, String age, String email,
                       String department, String subject, String designation, String gender,
                       String address, String photoPath, String registrationDate) {
            this.teacherId = teacherId;
            this.name = name;
            this.age = age;
            this.email = email;
            this.department = department;
            this.subject = subject;
            this.designation = designation;
            this.gender = gender;
            this.address = address;
            this.photoPath = photoPath;
            this.registrationDate = registrationDate;
        }

        public String getTeacherId() { return teacherId; }
        public String getName() { return name; }
        public String getAge() { return age; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getSubject() { return subject; }
        public String getDesignation() { return designation; }
        public String getGender() { return gender; }
        public String getAddress() { return address; }
        public String getPhotoPath() { return photoPath; }
        public String getRegistrationDate() { return registrationDate; }
    }
}
