# ====================================================================
# Inventory System Startup Script (PowerShell - Cross-Platform)
# This script runs the Spring Boot application with auto-reload support
# Works on Windows, Linux, and macOS (if PowerShell is installed)
# ====================================================================

Write-Host ""
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "  Inventory System - Development Mode (PowerShell)" -ForegroundColor Cyan
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven is available
$mvnCommand = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $mvnCommand) {
    Write-Host "[ERROR] Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven and add it to your PATH" -ForegroundColor Yellow
    exit 1
}

# Quick compile check
Write-Host "[1/2] Compiling project (quick check)..." -ForegroundColor Yellow
$compileResult = & mvn compile -q -DskipTests 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed. Please fix errors before running." -ForegroundColor Red
    Write-Host "Running full compile to show errors..." -ForegroundColor Yellow
    & mvn compile -DskipTests
    exit 1
} else {
    Write-Host "[OK] Compilation successful" -ForegroundColor Green
}
Write-Host ""

# Run application with Spring Boot Maven plugin
Write-Host "[2/2] Starting application with auto-reload..." -ForegroundColor Yellow
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host "Application URL:     http://localhost:8080" -ForegroundColor Blue
Write-Host "Swagger UI:          http://localhost:8080/swagger-ui.html" -ForegroundColor Blue
Write-Host "API Docs:            http://localhost:8080/api-docs" -ForegroundColor Blue
Write-Host "Default credentials: admin / admin123" -ForegroundColor Blue
Write-Host ""
Write-Host "[OK] Auto-reload enabled: Code changes will trigger automatic restart" -ForegroundColor Green
Write-Host "[OK] Hot reload:        Static resources and templates reload automatically" -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Starting Spring Boot Application..." -ForegroundColor Yellow
Write-Host "Press Ctrl+C to stop" -ForegroundColor Yellow
Write-Host ""

# Run with Spring Boot Maven plugin
& mvn spring-boot:run


