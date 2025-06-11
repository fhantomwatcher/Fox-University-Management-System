<p align="center">
  <img src="src/GUI/Resources/icon/fox.png" alt="Fox University Logo" width="120"/>
  <p align="center" style="color: orange; font-size: 20px;">
  Fox University Management System
  </p>
</p>




A Java Swing-based desktop application for managing student and faculty data at Fox University.

## Features

- **Student Registration:** Register new students with details and photo upload.
- **Teacher Registration:** Register new faculty members with subject, designation, and photo.
- **Student List:** View, filter, and search all registered students by department or keyword.
- **Teacher List:** View, filter, and search all registered teachers by department or keyword.
- **Photo Management:** Upload and store user photos in the application directory.
- **CSV Data Storage:** All user data is stored in CSV files for easy backup and portability.
- **Modern UI:** Clean, responsive interface with navigation panel and card-based layout.

## Project Structure

```
start.bat
src/
  GUI/
    SysGUI.java
    NavigationPanel.java
    panels/
      RegistrationPanel.java
      StudentPanel.java
      TeacherPanel.java
    Entity/
      Person.java
      Student.java
      Teacher.java
    Resources/
      fonts/
      icon/
  Main/
    SMSYSmain.java
  userdata/
    students.csv
    teachers.csv
    photos/
bin/
lib/
.vscode/
```

## Getting Started

### Prerequisites

- Java JDK 8 or higher (Recommanded Java JDK 21)
- (Optional) Visual Studio Code with Java extensions

### Build & Run

1. **Compile & Run:**
   - Double-click `start.bat` (Windows) or run in terminal:
     ```sh
     ./start.bat
     ```
   - This will compile the source code (if needed) and launch the application.

2. **Manual Compilation:**
   - Compile:
     ```sh
     javac -d bin -cp src src/Main/SMSYSmain.java src/GUI/*.java src/GUI/Entity/*.java src/GUI/panels/*.java
     ```
   - Run:
     ```sh
     java -cp bin Main.SMSYSmain
     ```

### Data Files

- Student and teacher data are stored in `src/userdata/students.csv` and `src/userdata/teachers.csv`.
- Uploaded photos are saved in `src/userdata/photos/`.

## Screenshots

*Will upload later*

## Authors

- [Fhantom Watcher]

## License

This project is for educational purposes.
