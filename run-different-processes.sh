#!/bin/bash

# Script to run players in different Java processes

echo "Building project..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

JAR_FILE="target/player-communication-1.0-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found: $JAR_FILE"
    exit 1
fi

echo ""
echo "Starting Player2 (responder) in background..."
java -cp "$JAR_FILE" com.player.DifferentProcessMain Player2 5002 Player1 5001 > player2.log 2>&1 &
PLAYER2_PID=$!
echo "Player2 started with PID: $PLAYER2_PID"

sleep 2

echo ""
echo "Starting Player1 (initiator)..."
java -cp "$JAR_FILE" com.player.DifferentProcessMain Player1 5001 Player2 5002 initiator

echo ""
echo "Player1 finished. Stopping Player2..."
kill $PLAYER2_PID 2>/dev/null
wait $PLAYER2_PID 2>/dev/null

echo ""
echo "Player2 log:"
cat player2.log
rm -f player2.log

echo ""
echo "All processes terminated."
