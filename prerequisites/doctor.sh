#!/bin/bash

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "=========================================="
echo "System Prerequisites Doctor Check"
echo "=========================================="
echo ""

# Track overall status
ALL_CHECKS_PASSED=true

# Function to print status
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓${NC} $2"
    else
        echo -e "${RED}✗${NC} $2"
        ALL_CHECKS_PASSED=false
    fi
}

# Check Java
echo "Checking Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    print_status 0 "Java is installed: $JAVA_VERSION"
else
    print_status 1 "Java is NOT installed"
fi
echo ""

# Check Docker
echo "Checking Docker..."
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    print_status 0 "Docker is installed: $DOCKER_VERSION"
    
    # Check if Docker daemon is running
    if docker ps &> /dev/null; then
        print_status 0 "Docker daemon is running"
    else
        print_status 1 "Docker is installed but daemon is NOT running"
    fi
else
    print_status 1 "Docker is NOT installed"
fi
echo ""

# Check Git
echo "Checking Git..."
if command -v git &> /dev/null; then
    GIT_VERSION=$(git --version)
    print_status 0 "Git is installed: $GIT_VERSION"
else
    print_status 1 "Git is NOT installed"
fi
echo ""

# Check GitHub CLI
echo "Checking GitHub CLI..."
if command -v gh &> /dev/null; then
    GH_VERSION=$(gh --version | head -n 1)
    print_status 0 "GitHub CLI is installed: $GH_VERSION"
    
    # Check GitHub CLI authentication
    if gh auth status &> /dev/null; then
        print_status 0 "GitHub CLI is authenticated"
    else
        print_status 1 "GitHub CLI is NOT authenticated (run: gh auth login)"
    fi
else
    print_status 1 "GitHub CLI is NOT installed"
fi
echo ""

# Check GCP CLI
echo "Checking GCP CLI..."
if command -v gcloud &> /dev/null; then
    GCLOUD_VERSION=$(gcloud --version | head -n 1)
    print_status 0 "GCP CLI is installed: $GCLOUD_VERSION"
    
    # Check GCP CLI authentication
    if gcloud config get-value account &> /dev/null; then
        GCLOUD_ACCOUNT=$(gcloud config get-value account 2>/dev/null)
        if [ -n "$GCLOUD_ACCOUNT" ]; then
            print_status 0 "GCP CLI is authenticated as: $GCLOUD_ACCOUNT"
        else
            print_status 1 "GCP CLI is NOT authenticated (run: gcloud auth login)"
        fi
    else
        print_status 1 "GCP CLI is NOT authenticated (run: gcloud auth login)"
    fi
else
    print_status 1 "GCP CLI is NOT installed"
fi
echo ""

# Check npm
echo "Checking npm..."
if command -v npm &> /dev/null; then
    NPM_VERSION=$(npm --version)
    print_status 0 "npm is installed: v$NPM_VERSION"
else
    print_status 1 "npm is NOT installed"
fi
echo ""

# Check Task (go-task)
echo "Checking Task..."
if command -v task &> /dev/null; then
    TASK_VERSION=$(task --version)
    print_status 0 "Task is installed: $TASK_VERSION"
else
    print_status 1 "Task is NOT installed (install: brew install go-task/tap/go-task)"
fi
echo ""

# Check OpenAI API Key
echo "Checking OpenAI API Key..."
if [ -z "$OPENAI_API_KEY" ]; then
    print_status 1 "OPENAI_API_KEY environment variable is NOT set"
    echo -e "${YELLOW}  Skipping OpenAI API test${NC}"
else
    print_status 0 "OPENAI_API_KEY environment variable is set"
    
    # Test OpenAI API call
    echo "Testing OpenAI API connection..."
    RESPONSE=$(curl -s -w "\n%{http_code}" https://api.openai.com/v1/chat/completions \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $OPENAI_API_KEY" \
        -d '{
            "model": "gpt-4o-mini",
            "messages": [{"role": "user", "content": "Say hello"}],
            "max_tokens": 10
        }')
    
    HTTP_CODE=$(echo "$RESPONSE" | tail -n1)
    RESPONSE_BODY=$(echo "$RESPONSE" | sed '$d')
    
    if [ "$HTTP_CODE" -eq 200 ]; then
        print_status 0 "OpenAI API test successful"
        MESSAGE=$(echo "$RESPONSE_BODY" | grep -o '"content":"[^"]*"' | head -n1 | cut -d'"' -f4)
        if [ -n "$MESSAGE" ]; then
            echo -e "${GREEN}  Response: $MESSAGE${NC}"
        fi
    else
        print_status 1 "OpenAI API test failed (HTTP $HTTP_CODE)"
        if echo "$RESPONSE_BODY" | grep -q "error"; then
            ERROR_MSG=$(echo "$RESPONSE_BODY" | grep -o '"message":"[^"]*"' | cut -d'"' -f4)
            echo -e "${RED}  Error: $ERROR_MSG${NC}"
        fi
    fi
fi
echo ""

# Summary
echo "=========================================="
if [ "$ALL_CHECKS_PASSED" = true ]; then
    echo -e "${GREEN}All checks passed! ✓${NC}"
    exit 0
else
    echo -e "${YELLOW}Some checks failed. Please install missing prerequisites.${NC}"
    exit 1
fi
