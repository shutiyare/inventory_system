#!/bin/bash

###############################################################################
# Inventory System Startup Script (Development Mode)
# This script runs the Spring Boot application with auto-reload support
# Changes to code will automatically trigger application restart
###############################################################################

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

echo ""
echo "================================================================"
echo -e "${BLUE}  Inventory System - Development Mode${NC}"
echo "================================================================"
echo ""

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}✗ Maven is not installed or not in PATH${NC}"
    exit 1
fi

# Quick compile check (optional - can be skipped for faster startup)
echo -e "${YELLOW}[1/2] Compiling project (quick check)...${NC}"
if mvn compile -q -DskipTests 2>&1 | grep -i "error\|failure" > /dev/null; then
    echo -e "${RED}✗ Compilation failed. Please fix errors before running.${NC}"
    echo -e "${YELLOW}Running full compile to show errors...${NC}"
    mvn compile -DskipTests
    exit 1
else
    echo -e "${GREEN}✓ Compilation successful${NC}"
fi
echo ""

# Run application with Spring Boot Maven plugin (supports auto-reload)
echo -e "${YELLOW}[2/2] Starting application with auto-reload...${NC}"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}Application URL:${NC}     http://localhost:8080"
echo -e "${BLUE}Swagger UI:${NC}          http://localhost:8080/swagger-ui.html"
echo -e "${BLUE}API Docs:${NC}            http://localhost:8080/api-docs"
echo -e "${BLUE}Default credentials:${NC} admin / admin123"
echo ""
echo -e "${GREEN}✓ Auto-reload enabled:${NC} Code changes will trigger automatic restart"
echo -e "${GREEN}✓ Hot reload:${NC}        Static resources and templates reload automatically"
echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo -e "${YELLOW}Starting Spring Boot Application...${NC}"
echo -e "${YELLOW}Press Ctrl+C to stop${NC}"
echo ""

# Run with Spring Boot Maven plugin
# This enables:
# - Fast startup (no JAR packaging needed)
# - Auto-reload on code changes (if spring-boot-devtools is configured)
# - Live compilation
mvn spring-boot:run

