#!/bin/bash

# Script to run players in the same Java process

# Use absolute path to Java to avoid working directory issues
JAVA_CMD=$(which java 2>/dev/null || echo "/usr/bin/java")

echo "Building project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "Running players in the same process..."
echo ""

mvn exec:java -Dexec.mainClass="com.player.SameProcessMain" -q
