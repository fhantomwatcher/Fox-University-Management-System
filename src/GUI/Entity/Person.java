package GUI.Entity;

import java.lang.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Person {
    protected String id;
    protected String name;
    protected String age;
    protected String email;
    protected String department;
    protected String gender;
    protected String address;
    protected String photoPath;
    protected String registrationDate;
    
    // Base directory for user data
    protected static final String USER_DATA_DIR = "src/userdata";
    protected static final String PHOTOS_DIR = USER_DATA_DIR + File.separator + "photos";
    
    public Person(String name, String age, String email, String department, 
                  String gender, String address, String photoPath) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.department = department;
        this.gender = gender;
        this.address = address;
        this.photoPath = photoPath;
        this.registrationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        // Create necessary directories
        createDirectories();
    }
    
    // Create userdata and photos directories if they don't exist
    protected void createDirectories() {
        try {
            Files.createDirectories(Paths.get(USER_DATA_DIR));
            Files.createDirectories(Paths.get(PHOTOS_DIR));
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }
    
    // Copy photo to photos directory with ID as filename
    protected String savePhoto(String originalPhotoPath, String newFileName) {
        if (originalPhotoPath == null || originalPhotoPath.isEmpty()) {
            return "";
        }
        
        try {
            Path sourcePath = Paths.get(originalPhotoPath);
            if (!Files.exists(sourcePath)) {
                return "";
            }
            
            // Get file extension
            String fileName = sourcePath.getFileName().toString();
            String extension = "";
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                extension = fileName.substring(lastDot);
            }
            
            // Create destination path with ID as filename
            Path destinationPath = Paths.get(PHOTOS_DIR, newFileName + extension);
            
            // Copy file
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            return destinationPath.toString();
        } catch (IOException e) {
            System.err.println("Error saving photo: " + e.getMessage());
            return "";
        }
    }
    
    // Abstract methods to be implemented by subclasses
    public abstract void saveToCSV();
    public abstract String generateId();
    public abstract String getCSVHeader();
    public abstract String toCSVString();
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAge() { return age; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getGender() { return gender; }
    public String getAddress() { return address; }
    public String getPhotoPath() { return photoPath; }
    public String getRegistrationDate() { return registrationDate; }
    
    // Utility method to escape CSV values
    protected String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}