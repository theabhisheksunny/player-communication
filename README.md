# Player Communication System

A clean, simple Java implementation of inter-player message exchange supporting both same-process and different-process communication.

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Project Structure

```
player-communication/
├── pom.xml
├── run-same-process.sh
├── run-different-processes.sh
├── README.md
└── src/main/java/com/player/
    ├── Message.java                    # Message data structure
    ├── MessageHandler.java             # Message reception interface
    ├── MessageSender.java              # Message sending interface
    ├── Player.java                     # Core Player class
    ├── InProcessMessageSender.java     # Same-process message routing
    ├── SocketMessageSender.java        # Inter-process message sending
    ├── SocketMessageReceiver.java      # Inter-process message receiving
    ├── SameProcessMain.java            # Entry point for scenario 5
    └── DifferentProcessMain.java       # Entry point for scenario 7
```

## Design Overview

### Core Classes

- **Player**: Central class representing a player. Maintains identity, tracks sent messages, processes incoming messages, and generates responses.

- **Message**: Immutable data container for message content and sender identity.

- **MessageHandler**: Interface for receiving messages. Player implements this.

- **MessageSender**: Abstraction for sending messages. Enables different communication strategies.

### Communication Strategies

**Same Process (Requirement 5):**
- **InProcessMessageSender**: Maintains an in-memory registry of players and routes messages directly via method calls.

**Different Processes (Requirement 7):**
- **SocketMessageSender**: Sends serialized messages over TCP sockets.
- **SocketMessageReceiver**: Listens on a port and deserializes incoming messages.

## Running the Application

### Scenario 5: Same Process

```bash
./run-same-process.sh
```

This starts both Player1 and Player2 in a single JVM process. Player1 initiates the exchange, and the program terminates gracefully after Player1 has sent and received 10 messages.

### Scenario 7: Different Processes

```bash
./run-different-processes.sh
```

This starts Player2 in the background, then starts Player1 in the foreground. Each player runs in its own JVM process with different PIDs. Player1 initiates and the script cleans up both processes after completion.

## Manual Execution

### Build

```bash
mvn clean package
```

### Same Process

```bash
mvn exec:java -Dexec.mainClass="com.player.SameProcessMain"
```

### Different Processes (Manual)

Terminal 1:
```bash
/usr/bin/java -cp target/player-communication-1.0-SNAPSHOT.jar com.player.DifferentProcessMain Player2 5002 Player1 5001
```

Terminal 2:
```bash
/usr/bin/java -cp target/player-communication-1.0-SNAPSHOT.jar com.player.DifferentProcessMain Player1 5001 Player2 5002 initiator
```

**Note:** If you get "Properties init: Could not determine current working directory" error, use `/usr/bin/java` instead of `java`.

## How It Works

1. **Initialization**: Two players are created with unique IDs.

2. **Message Exchange**:
   - Player1 (initiator) sends "Hello" to Player2
   - Player2 receives "Hello", concatenates its message counter (0), and sends back "Hello0"
   - Player1 receives "Hello0", concatenates its counter (1), and sends back "Hello01"
   - This continues, building up the message content with each exchange

3. **Stop Condition**: When Player1's sent message count reaches 10, the program terminates gracefully.

4. **Expected Message Sequence**:
   - Player1 → Player2: "Hello"
   - Player2 → Player1: "Hello0"
   - Player1 → Player2: "Hello01"
   - Player2 → Player1: "Hello012"
   - ... and so on until Player1 sends 10 messages

## Design Principles

- **Separation of Concerns**: Player logic is independent of communication mechanism.
- **Dependency Inversion**: Player depends on MessageSender abstraction, not concrete implementations.
- **Single Responsibility**: Each class has one clear purpose.
- **Open/Closed**: New communication strategies can be added without modifying Player.
- **Pure Java**: No external frameworks, only standard Java libraries.
- **Simplicity**: Minimal technology, maximum clarity.

## Class Responsibilities Summary

| Class | Responsibility |
|-------|----------------|
| Player | Player identity, message counting, message processing logic |
| Message | Encapsulate message data |
| MessageHandler | Define callback for message reception |
| MessageSender | Abstract message delivery |
| InProcessMessageSender | Route messages within same JVM |
| SocketMessageSender | Send messages across processes via TCP |
| SocketMessageReceiver | Receive messages across processes via TCP |
| SameProcessMain | Bootstrap same-process scenario |
| DifferentProcessMain | Bootstrap different-process scenario |
