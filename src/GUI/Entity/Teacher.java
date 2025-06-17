package GUI.Entity;

import java.lang.*;
import java.io.*;
import java.nio.file.*;
import java.util.Random;

public class Teacher extends Person {
    private String subject;
    private String designation;
    
    private static final String CSV_FILE = USER_DATA_DIR + File.separator + "teachers.csv";
    private static final String ID_PREFIX = "TCH";
    
    public Teacher(String name, String age, String email, String department, 
                   String subject, String designation, String gender, String address, String photoPath) {
        super(name, age, email, department, gender, address, photoPath);
        this.subject = subject;
        this.designation = designation;
        this.id = generateId();
        
        // Save photo with teacher ID as filename
        if (photoPath != null && !photoPath.isEmpty()) {
            this.photoPath = savePhoto(photoPath, this.id);
        }
    }
    
    @Override
    public String generateId() {
        // Generate a random 6-digit number
        Random random = new Random();
        int randomNum = 100000 + random.nextInt(900000);
        return ID_PREFIX + "-" + randomNum;
    }
    
    @Override
    public String getCSVHeader() {
        return "ID,Name,Age,Email,Department,Subject,Designation,Gender,Address,PhotoPath,RegistrationDate";
    }
    
    @Override
    public String toCSVString() {
        return String.join(",",
            escapeCSV(id),
            escapeCSV(name),
            escapeCSV(age),
            escapeCSV(email),
            escapeCSV(department),
            escapeCSV(subject),
            escapeCSV(designation),
            escapeCSV(gender),
            escapeCSV(address),
            escapeCSV(photoPath),
            escapeCSV(registrationDate)
        );
    }
    
    @Override
    public void saveToCSV() {
        try {
            Path csvPath = Paths.get(CSV_FILE);
            boolean fileExists = Files.exists(csvPath);
            
            // Create file if it doesn't exist and write header
            if (!fileExists) {
                try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(csvPath))) {
                    writer.println(getCSVHeader());
                }
            }
            
            // Append teacher data
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(csvPath, 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
                writer.println(toCSVString());
            }
            
            System.out.println("Teacher data saved successfully to " + CSV_FILE);
            
        } catch (IOException e) {
            System.err.println("Error saving teacher data to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to save teacher data", e);
        }
    }
    
    // Static method to read all teachers from CSV
    public static java.util.List<Teacher> loadAllFromCSV() {
        java.util.List<Teacher> teachers = new java.util.ArrayList<>();
        Path csvPath = Paths.get(CSV_FILE);
        
        if (!Files.exists(csvPath)) {
            return teachers;
        }
        
        try (java.io.BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                
                String[] parts = parseCSVLine(line);
                if (parts.length >= 11) {
                    Teacher teacher = new Teacher(parts[1], parts[2], parts[3], parts[4], 
                                                parts[5], parts[6], parts[7], parts[8], parts[9]);
                    teacher.id = parts[0];
                    teacher.registrationDate = parts[10];
                    teachers.add(teacher);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading teachers from CSV: " + e.getMessage());
        }
        
        return teachers;
    }
    
    // Utility method to parse CSV line handling quoted values
    private static String[] parseCSVLine(String line) {
        java.util.List<String> result = new java.util.ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentField.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        result.add(currentField.toString());
        
        return result.toArray(new String[0]);
    }
    
    // Getters for teacher-specific fields
    public String getSubject() { return subject; }
    public String getDesignation() { return designation; }
}