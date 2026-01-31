#!/bin/bash

# CDSL Java Development Environment - Development Helper Scripts

# Function to build the project
build() {
    echo "🔨 Building Maven project..."
    mvn clean install "$@"
}

# Function to run tests
run_tests() {
    echo "🧪 Running tests..."
    if [ -f "pom.xml" ]; then
        mvn test "$@"
    else
        echo "⚠️  pom.xml not found - cannot run tests"
    fi
}

# Function to format code
format_code() {
    echo "🎨 Formatting code with Maven..."
    if [ -f "pom.xml" ]; then
        # Check if spotless or formatter plugin is configured
        if grep -q "spotless-maven-plugin\|formatter-maven-plugin" pom.xml 2>/dev/null; then
            mvn spotless:apply 2>/dev/null || mvn formatter:format 2>/dev/null || echo "⚠️  No formatter plugin configured"
        else
            echo "ℹ️  No formatter plugin detected in pom.xml"
            echo "   Consider adding spotless-maven-plugin or formatter-maven-plugin"
        fi
    else
        echo "❌ pom.xml not found"
    fi
}

# Function to clean project
clean() {
    echo "🧹 Cleaning project..."
    mvn clean "$@"
}

# Function to show project status
show_status() {
    echo "📊 CDSL Java Development Environment Status:"
    echo "=============================================="
    echo "📁 Working directory: $(pwd)"
    if command -v java &> /dev/null; then
        echo "☕ Java version: $(java -version 2>&1 | head -n 1)"
    fi
    if command -v mvn &> /dev/null; then
        echo "🔧 Maven version: $(mvn -version 2>&1 | head -n 1)"
    fi
    echo ""
    echo "🔧 Git status:"
    git status --short 2>/dev/null || echo "⚠️  Not a git repository"
    echo ""
    echo "📋 Available commands:"
    echo "  ./dev_helpers.sh build [args]    - Build Maven project"
    echo "  ./dev_helpers.sh test [args]     - Run tests"
    echo "  ./dev_helpers.sh clean [args]    - Clean project"
    echo "  ./dev_helpers.sh format          - Format code"
    echo "  ./dev_helpers.sh status          - Show this status"
}

# Main command dispatcher
case "$1" in
    "build")
        shift
        build "$@"
        ;;
    "test")
        shift
        run_tests "$@"
        ;;
    "clean")
        shift
        clean "$@"
        ;;
    "format")
        format_code
        ;;
    "status"|"")
        show_status
        ;;
    *)
        echo "❌ Unknown command: $1"
        show_status
        exit 1
        ;;
esac
