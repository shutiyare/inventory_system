@echo off
REM ====================================================================
REM Inventory System Startup Script (Windows)
REM This script runs the Spring Boot application with auto-reload support
REM Changes to code will automatically trigger application restart
REM ====================================================================

setlocal enabledelayedexpansion

echo.
echo ================================================================
echo   Inventory System - Development Mode (Windows)
echo ================================================================
echo.

REM Check if Maven is available
where mvn >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    exit /b 1
)

REM Quick compile check
echo [1/2] Compiling project (quick check)...
call mvn compile -q -DskipTests >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed. Please fix errors before running.
    echo Running full compile to show errors...
    call mvn compile -DskipTests
    exit /b 1
) else (
    echo [OK] Compilation successful
)
echo.

REM Run application with Spring Boot Maven plugin
echo [2/2] Starting application with auto-reload...
echo ================================================================
echo Application URL:     http://localhost:8080
echo Swagger UI:          http://localhost:8080/swagger-ui.html
echo API Docs:            http://localhost:8080/api-docs
echo Default credentials: admin / admin123
echo.
echo [OK] Auto-reload enabled: Code changes will trigger automatic restart
echo [OK] Hot reload:        Static resources and templates reload automatically
echo ================================================================
echo.
echo Starting Spring Boot Application...
echo Press Ctrl+C to stop
echo.

REM Run with Spring Boot Maven plugin
call mvn spring-boot:run

endlocal


