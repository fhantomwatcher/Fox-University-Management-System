package GUI.Entity;

import java.lang.*;
import java.io.*;
import java.nio.file.*;
import java.util.Random;

public class Student extends Person {
    private static final String CSV_FILE = USER_DATA_DIR + File.separator + "students.csv";
    private static final String ID_PREFIX = "STU";
    
    public Student(String name, String age, String email, String department, 
                   String gender, String address, String photoPath) {
        super(name, age, email, department, gender, address, photoPath);
        this.id = generateId();
        
        // Save photo with student ID as filename
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
        return "ID,Name,Age,Email,Department,Gender,Address,PhotoPath,RegistrationDate";
    }
    
    @Override
    public String toCSVString() {
        return String.join(",",
            escapeCSV(id),
            escapeCSV(name),
            escapeCSV(age),
            escapeCSV(email),
            escapeCSV(department),
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
            
            // Append student data
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(csvPath, 
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
                writer.println(toCSVString());
            }
            
            System.out.println("Student data saved successfully to " + CSV_FILE);
            
        } catch (IOException e) {
            System.err.println("Error saving student data to CSV: " + e.getMessage());
            throw new RuntimeException("Failed to save student data", e);
        }
    }
    
    // Static method to read all students from CSV
    public static java.util.List<Student> loadAllFromCSV() {
        java.util.List<Student> students = new java.util.ArrayList<>();
        Path csvPath = Paths.get(CSV_FILE);
        
        if (!Files.exists(csvPath)) {
            return students;
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
                if (parts.length >= 9) {
                    Student student = new Student(parts[1], parts[2], parts[3], parts[4], 
                                                parts[5], parts[6], parts[7]);
                    student.id = parts[0];
                    student.registrationDate = parts[8];
                    students.add(student);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students from CSV: " + e.getMessage());
        }
        
        return students;
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
}