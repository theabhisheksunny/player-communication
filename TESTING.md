# Testing Guide

This document describes how to verify that the Player Communication System works correctly according to all requirements.

## Requirements Checklist

- [x] **Requirement 1**: Create 2 players - ✓ Implemented in both scenarios
- [x] **Requirement 2**: One player sends message to second player (initiator) - ✓ Player1 is the initiator
- [x] **Requirement 3**: Receiver sends back message with content + message counter - ✓ Implemented in Player.onMessageReceived()
- [x] **Requirement 4**: Stop after initiator sent 10 and received 10 messages - ✓ Checked in both Main classes
- [x] **Requirement 5**: Both players in same Java process - ✓ SameProcessMain
- [x] **Requirement 6**: Document class responsibilities - ✓ Javadoc on all classes
- [x] **Requirement 7**: Every player in separate Java process - ✓ DifferentProcessMain

## Expected Output - Same Process

When you run `./run-same-process.sh`, you should see:

```
=== Starting Same Process Communication ===

Player1 (initiator) and Player2 created in PID: <some_pid>
Starting message exchange...

[Player1] Sent message #1: Hello
[Player2] Received: Hello
[Player2] Sent message #1: Hello0
[Player1] Received: Hello0
[Player1] Sent message #2: Hello01
[Player2] Received: Hello01
[Player2] Sent message #2: Hello012
[Player1] Received: Hello012
[Player1] Sent message #3: Hello0123
[Player2] Received: Hello0123
[Player2] Sent message #3: Hello01234
[Player1] Received: Hello01234
[Player1] Sent message #4: Hello012345
[Player2] Received: Hello012345
[Player2] Sent message #4: Hello0123456
[Player1] Received: Hello0123456
[Player1] Sent message #5: Hello01234567
[Player2] Received: Hello01234567
[Player2] Sent message #5: Hello012345678
[Player1] Received: Hello012345678
[Player1] Sent message #6: Hello0123456789
[Player2] Received: Hello0123456789
[Player2] Sent message #6: Hello01234567890
[Player1] Received: Hello01234567890
[Player1] Sent message #7: Hello012345678901
[Player2] Received: Hello012345678901
[Player2] Sent message #7: Hello0123456789012
[Player1] Received: Hello0123456789012
[Player1] Sent message #8: Hello01234567890123
[Player2] Received: Hello01234567890123
[Player2] Sent message #8: Hello012345678901234
[Player1] Received: Hello012345678901234
[Player1] Sent message #9: Hello0123456789012345
[Player2] Received: Hello0123456789012345
[Player2] Sent message #9: Hello01234567890123456
[Player1] Received: Hello01234567890123456
[Player1] Sent message #10: Hello012345678901234567

=== Stop Condition Met ===
Player1 sent: 10 messages
Player2 sent: 9 messages
Terminating gracefully.
```

## Expected Output - Different Processes

When you run `./run-different-processes.sh`, you should see similar output but with different PIDs:

Player2 (in background):
```
=== Starting Player2 in separate process ===
PID: <pid_2>
Local port: 5002
Remote player: Player1 on port 5001
Role: Responder

Listening on port 5002
Waiting for messages...
```

Player1 (foreground):
```
=== Starting Player1 in separate process ===
PID: <pid_1>
Local port: 5001
Remote player: Player2 on port 5002
Role: Initiator

Listening on port 5001
Initiating message exchange...

[Player1] Sent message #1: Hello
[Player1] Received: Hello0
[Player1] Sent message #2: Hello01
...
[Player1] Sent message #10: Hello012345678901234567

=== Stop Condition Met ===
Player1 sent: 10 messages
Terminating gracefully.
```

Note: `<pid_1>` and `<pid_2>` should be **different** PIDs, proving they run in separate processes.

## Verification Steps

### 1. Verify Same Process (Requirement 5)

```bash
./run-same-process.sh
```

**Check:**
- Both players show the same PID
- Message exchange completes successfully
- Player1 sends exactly 10 messages
- Player2 sends exactly 9 messages (receives first, then responds)
- Program terminates gracefully

### 2. Verify Different Processes (Requirement 7)

```bash
./run-different-processes.sh
```

**Check:**
- Player1 and Player2 show different PIDs
- Both processes are visible with `ps` command during execution
- Message exchange works across network sockets
- Player1 sends exactly 10 messages
- Program terminates gracefully and cleans up both processes

### 3. Verify Message Content (Requirement 3)

Examine the output logs and verify:
- Each response contains the received message content
- Each response appends the sender's message counter
- Pattern: "Hello" → "Hello0" → "Hello01" → "Hello012" → ...

### 4. Verify Stop Condition (Requirement 4)

Check that:
- Program stops when Player1 (initiator) has sent 10 messages
- Program does not continue beyond this point
- No error messages or exceptions during termination

### 5. Verify Class Responsibilities (Requirement 6)

Open each Java file and verify:
- Each class has a Javadoc comment at the top
- The comment includes "Responsibilities:" section
- Each responsibility is clearly stated

## Manual Testing in Separate Terminals

For more control over the different-process scenario:

**Terminal 1:**
```bash
cd player-communication
java -cp target/player-communication-1.0-SNAPSHOT.jar com.player.DifferentProcessMain Player2 5002 Player1 5001
```

**Terminal 2:**
```bash
cd player-communication
java -cp target/player-communication-1.0-SNAPSHOT.jar com.player.DifferentProcessMain Player1 5001 Player2 5002 initiator
```

**Verification:**
1. Start Terminal 1 first and note the PID
2. Start Terminal 2 and note its PID (should be different)
3. Watch the message exchange in both terminals
4. Verify Player1 terminal shows 10 sent messages and terminates
5. Manually stop Player2 in Terminal 1 (Ctrl+C)

## Troubleshooting Tests

### Test fails with compilation error
- Ensure Java JDK 11+ is installed
- Run `mvn clean compile` to see detailed errors

### Different process test hangs
- Ensure Player2 starts before Player1
- Check ports 5001 and 5002 are not in use: `lsof -i :5001` and `lsof -i :5002`

### Messages not received
- Check firewall settings allow localhost connections
- Verify no antivirus blocking Java network access

### Incorrect message count
- Review Player.java logic for message counter increment
- Check stop condition in main classes

## Success Criteria

The system passes all tests if:

1. ✓ Same-process scenario runs successfully with single PID
2. ✓ Different-process scenario runs with two different PIDs
3. ✓ Message content follows expected pattern (concatenation + counter)
4. ✓ Stop condition triggers at exactly 10 sent messages from initiator
5. ✓ Graceful termination with no errors
6. ✓ All classes have documented responsibilities
7. ✓ Clean, understandable code design
