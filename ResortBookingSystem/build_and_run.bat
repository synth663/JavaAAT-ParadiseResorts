@echo off
echo ===================================
echo Resort Reservation System Builder
echo ===================================

REM Create output directory
if not exist out mkdir out
if not exist lib mkdir lib
if not exist data mkdir data

REM Check for SQLite JDBC driver
if not exist lib\sqlite-jdbc*.jar (
    echo.
    echo WARNING: SQLite JDBC driver not found!
    echo Please download from: https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    echo Place the JAR file in the lib\ folder
    echo.
    pause
    exit /b 1
)

echo.
echo Compiling Java files...

REM Compile all Java files
javac -cp "lib\*" -d out -sourcepath src ^
    src\database\*.java ^
    src\utils\*.java ^
    src\models\*.java ^
    src\dao\*.java ^
    src\views\*.java ^
    src\views\customer\*.java ^
    src\views\admin\*.java ^
    src\ResortReservationApp.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo Compilation FAILED!
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo ===================================
echo Running application...
echo ===================================

java -cp "out;lib\*" ResortReservationApp

pause
