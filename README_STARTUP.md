# Startup Scripts Guide

This project includes multiple startup scripts for different operating systems to run the Spring Boot application in development mode with auto-reload support.

## Available Scripts

### 1. `startup.sh` (Linux/macOS/Unix)
Bash script for Unix-like systems (Linux, macOS, Unix).

**Usage:**
```bash
./startup.sh
```

**Or:**
```bash
bash startup.sh
```

**Requirements:**
- Bash shell
- Maven installed and in PATH

---

### 2. `startup.bat` (Windows)
Batch script for Windows Command Prompt.

**Usage:**
```cmd
startup.bat
```

**Or double-click the file in Windows Explorer**

**Requirements:**
- Windows Command Prompt (cmd.exe)
- Maven installed and in PATH

---

### 3. `startup.ps1` (PowerShell - Cross-Platform)
PowerShell script that works on Windows, Linux, and macOS (if PowerShell is installed).

**Usage on Windows:**
```powershell
.\startup.ps1
```

**Usage on Linux/macOS:**
```bash
pwsh startup.ps1
# or
powershell startup.ps1
```

**Requirements:**
- PowerShell (PowerShell Core 6+ for cross-platform support)
- Maven installed and in PATH

---

## Features

All scripts provide:
- ✅ **Quick compilation check** before starting
- ✅ **Auto-reload support** - Application restarts automatically on code changes
- ✅ **Hot reload** - Static resources reload without full restart
- ✅ **Fast startup** - No JAR packaging needed
- ✅ **Clear output** - Shows URLs and credentials

## How Auto-Reload Works

The application uses **Spring Boot DevTools** which:
1. Monitors classpath changes
2. Automatically restarts the application when `.class` files change
3. Reloads static resources (HTML, CSS, JS) without restart
4. Provides faster restart times (only reloads changed classes)

## Prerequisites

1. **Java 23+** installed
2. **Maven 3.9+** installed and in PATH
3. **PostgreSQL** running (default: localhost:5432)

## Troubleshooting

### Script not executable (Linux/macOS)
```bash
chmod +x startup.sh
```

### Maven not found
- Ensure Maven is installed: `mvn --version`
- Add Maven to your system PATH
- On Windows: Add Maven `bin` directory to System Environment Variables

### PowerShell execution policy (Windows)
If you get an execution policy error:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Port already in use
If port 8080 is already in use:
- Change port in `application.properties`: `server.port=8081`
- Or stop the application using the port

## Development Workflow

1. Run the appropriate startup script for your OS
2. Wait for application to start
3. Make code changes in your IDE
4. Save the file - application will automatically restart
5. See changes immediately without manual restart

## Production Deployment

For production, use the JAR file:
```bash
mvn clean package -DskipTests
java -jar target/inventory_system-*.jar
```

---

**Note:** These scripts are optimized for development. For production, always build and run the JAR file.


