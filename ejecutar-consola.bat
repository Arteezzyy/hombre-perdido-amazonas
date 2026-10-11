@echo off
rem ============================================================
rem  Hombre Perdido en el Amazonas - Version de consola (BFS)
rem ============================================================
chcp 65001 >nul
cd /d "%~dp0"

echo Compilando...
javac -cp "lib/*" -d out src/main/java/com/amazonas/*.java
if errorlevel 1 (
    echo.
    echo *** ERROR de compilacion ***
    pause
    exit /b 1
)

echo Ejecutando version de consola...
java -cp "out;lib/*" com.amazonas.Main
pause
