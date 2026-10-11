@echo off
rem ============================================================
rem  Hombre Perdido en el Amazonas - Compilar y ejecutar la GUI
rem  (BFS vs A*) con FlatLaf + Ikonli
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

echo Ejecutando visualizador BFS vs A*...
java -cp "out;lib/*" com.amazonas.VisualizadorBFS
