#!/bin/bash

# Manual compilation script (alternative to Maven)

echo "Manual compilation script for Player Communication"
echo ""

# Create output directory
mkdir -p target/classes

echo "Compiling Java files..."

# Find Java compiler
if ! command -v javac &> /dev/null; then
    echo "ERROR: javac not found. Please install Java JDK 11 or higher."
    echo ""
    echo "On Ubuntu/Debian:"
    echo "  sudo apt-get update"
    echo "  sudo apt-get install openjdk-11-jdk maven"
    echo ""
    echo "On macOS (with Homebrew):"
    echo "  brew install openjdk@11 maven"
    echo ""
    exit 1
fi

# Compile all Java files
javac -d target/classes src/main/java/com/player/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Compilation successful!"
echo ""
echo "To run same process scenario:"
echo "  java -cp target/classes com.player.SameProcessMain"
echo ""
echo "To run different processes scenario:"
echo "  Terminal 1: java -cp target/classes com.player.DifferentProcessMain Player2 5002 Player1 5001"
echo "  Terminal 2: java -cp target/classes com.player.DifferentProcessMain Player1 5001 Player2 5002 initiator"
