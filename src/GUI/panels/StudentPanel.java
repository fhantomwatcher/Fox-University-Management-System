package GUI.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private Map<String, List<Student>> departmentStudents = new HashMap<>();
    private JComboBox<String> departmentFilter;
    private JTextField searchField;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private String csvFilePath = "src/userdata/students.csv";
    private static final int PHOTO_WIDTH = 80;
    private static final int PHOTO_HEIGHT = 100;

    public StudentPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadStudentsFromCSV();
        setupLayout();
        departmentFilter.addActionListener(e -> filterByDepartment());
        displayStudents("All");
    }

    private void initializeComponents() {
        departmentFilter = new JComboBox<>();
        departmentFilter.addItem("All");

        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchStudents());

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

        // Mouse wheel scrolling (optional: for extra smoothness)
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

        JLabel titleLabel = new JLabel("Student List", SwingConstants.CENTER);
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
        searchBtn.addActionListener(e -> searchStudents());
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

    private void loadStudentsFromCSV() {
        departmentStudents.clear();

        ActionListener[] listeners = departmentFilter.getActionListeners();
        for (ActionListener listener : listeners) {
            departmentFilter.removeActionListener(listener);
        }

        departmentFilter.removeAllItems();
        departmentFilter.addItem("All");

        File csvFile = new File(csvFilePath);
        if (!csvFile.exists()) {
            JOptionPane.showMessageDialog(this, "No student data found. Please register students first.");
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
                if (data.size() >= 9) {
                    String id = data.get(0).trim();
                    String name = data.get(1).trim();
                    String age = data.get(2).trim();
                    String email = data.get(3).trim();
                    String department = data.get(4).trim();
                    String gender = data.get(5).trim();
                    String address = data.get(6).trim();
                    String photoPath = data.get(7).trim().replace("\\", "/");
                    String registrationDate = data.get(8).trim();

                    Student student = new Student(
                        id, name, age, email, department, gender, address, photoPath, registrationDate
                    );

                    departmentStudents.computeIfAbsent(department, k -> new ArrayList<>()).add(student);

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
            JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage());
        }

        for (ActionListener listener : listeners) {
            departmentFilter.addActionListener(listener);
        }
    }

    private void displayStudents(String selectedDepartment) {
        if (selectedDepartment == null) selectedDepartment = "All";
        contentPanel.removeAll();

        if (selectedDepartment.equals("All")) {
            List<String> sortedDepts = new ArrayList<>(departmentStudents.keySet());
            Collections.sort(sortedDepts);
            for (String dept : sortedDepts) {
                addDepartmentSection(dept, departmentStudents.get(dept));
            }
        } else {
            List<Student> students = departmentStudents.get(selectedDepartment);
            if (students != null) {
                addDepartmentSection(selectedDepartment, students);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addDepartmentSection(String department, List<Student> students) {
        JPanel deptHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deptHeaderPanel.setBackground(new Color(245, 245, 245));
        deptHeaderPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JLabel deptLabel = new JLabel(department + " (" + students.size() + " students)");
        deptLabel.setFont(new Font("Arial", Font.BOLD, 18));
        deptLabel.setForeground(new Color(70, 130, 180));
        deptHeaderPanel.add(deptLabel);

        contentPanel.add(deptHeaderPanel);

        // Responsive grid: 2 columns for wide screens, 1 for narrow
        int columns = Math.max(1, contentPanel.getWidth() / 400); // 400px per card
        if (columns < 2) columns = 2; // Default to 2 columns

        JPanel studentsGrid = new JPanel(new GridBagLayout());
        studentsGrid.setBackground(new Color(245, 245, 245));
        studentsGrid.setBorder(new EmptyBorder(0, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            gbc.gridx = i % columns;
            gbc.gridy = i / columns;
            studentsGrid.add(createStudentCard(student), gbc);
        }

        contentPanel.add(studentsGrid);
    }

    private JPanel createStudentCard(Student student) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        // Photo on the left
        JLabel photoLabel = createPhotoLabel(student.getPhotoPath());
        photoLabel.setPreferredSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setMinimumSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setMaximumSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);

        // Details on the right
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(student.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(70, 130, 180));

        JLabel idLabel = new JLabel("ID: " + student.getStudentId());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        idLabel.setForeground(Color.GRAY);

        JLabel deptLabel = new JLabel("Dept: " + student.getDepartment());
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel ageGenderLabel = new JLabel("Age: " + student.getAge() + " | " + student.getGender());
        ageGenderLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel emailLabel = new JLabel("Email: " + student.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Address (multi-line, wraps, using HTML for auto-wrapping)
        JLabel addressLabel = new JLabel("<html><body style='width:220px'>Address: " + student.getAddress() + "</body></html>");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        addressLabel.setForeground(new Color(60, 60, 60));

        JLabel regLabel = new JLabel("Registered: " + student.getRegistrationDate());
        regLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        regLabel.setForeground(new Color(120, 120, 120));

        infoPanel.add(nameLabel);
        infoPanel.add(idLabel);
        infoPanel.add(deptLabel);
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
        displayStudents(selected);
    }

    private void searchStudents() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            displayStudents((String) departmentFilter.getSelectedItem());
            return;
        }

        contentPanel.removeAll();

        for (String dept : departmentStudents.keySet()) {
            List<Student> filteredStudents = new ArrayList<>();
            for (Student student : departmentStudents.get(dept)) {
                if (
                        student.getName().toLowerCase().contains(searchTerm) ||
                        student.getStudentId().toLowerCase().contains(searchTerm) ||
                        student.getEmail().toLowerCase().contains(searchTerm) ||
                        student.getDepartment().toLowerCase().contains(searchTerm) ||
                        student.getGender().toLowerCase().contains(searchTerm) ||
                        student.getAddress().toLowerCase().contains(searchTerm)
                ) {
                    filteredStudents.add(student);
                }
            }
            if (!filteredStudents.isEmpty()) {
                addDepartmentSection(dept, filteredStudents);
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void refreshData() {
        loadStudentsFromCSV();
        displayStudents("All");
        searchField.setText("");
        if (departmentFilter.getItemCount() > 0) {
            departmentFilter.setSelectedItem("All");
        }
    }

    // Student class for your CSV structure
    private static class Student {
        private String studentId, name, age, email, department, gender, address, photoPath, registrationDate;

        public Student(String studentId, String name, String age, String email,
                       String department, String gender, String address, String photoPath, String registrationDate) {
            this.studentId = studentId;
            this.name = name;
            this.age = age;
            this.email = email;
            this.department = department;
            this.gender = gender;
            this.address = address;
            this.photoPath = photoPath;
            this.registrationDate = registrationDate;
        }

        public String getStudentId() { return studentId; }
        public String getName() { return name; }
        public String getAge() { return age; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
        public String getGender() { return gender; }
        public String getAddress() { return address; }
        public String getPhotoPath() { return photoPath; }
        public String getRegistrationDate() { return registrationDate; }
    }
}