@echo off
REM Check if bin directory exists and contains class files
if not exist bin\Main\SMSYSmain.class (
    echo Compiling Java source files...
    if not exist bin (
        mkdir bin
    )
    javac -d bin -cp src src\Main\SMSYSmain.java src\GUI\*.java src\GUI\Entity\*.java src\GUI\panels\*.java
    REM Copy resource files (fonts, icons, etc.) to bin directory
    xcopy /E /Y /I src\GUI\Resources bin\GUI\Resources
) else (
    echo Compiled files found. Skipping compilation.
)

REM Run the main class (Main.SMSYSmain)
java -cp bin Main.SMSYSmain

pause