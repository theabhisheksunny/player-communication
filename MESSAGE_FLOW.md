# Message Flow Diagram

## Same Process Communication

```
┌─────────────────────────────────────────────────────────┐
│                    JVM Process (Single PID)              │
│                                                          │
│  ┌──────────┐            ┌──────────────────┐          │
│  │ Player1  │            │ InProcessMessage │          │
│  │          │───send────>│     Sender       │          │
│  │ counter: │            │  (registry)      │          │
│  │    0→10  │<──deliver──│                  │          │
│  └──────────┘            └──────────────────┘          │
│       │                           │                     │
│       └───────────────┬───────────┘                     │
│                       │                                 │
│                       ▼                                 │
│  ┌──────────┐            ┌──────────────────┐          │
│  │ Player2  │<──deliver──│                  │          │
│  │          │            │                  │          │
│  │ counter: │───send────>│                  │          │
│  │    0→9   │            │                  │          │
│  └──────────┘            └──────────────────┘          │
└─────────────────────────────────────────────────────────┘
```

## Different Process Communication

```
┌──────────────────────────────┐    ┌──────────────────────────────┐
│  JVM Process 1 (PID: XXXX)   │    │  JVM Process 2 (PID: YYYY)   │
│                               │    │                               │
│  ┌──────────┐                │    │                ┌──────────┐  │
│  │ Player1  │                │    │                │ Player2  │  │
│  │          │                │    │                │          │  │
│  │ counter: │                │    │                │ counter: │  │
│  │   0→10   │                │    │                │   0→9    │  │
│  └────┬─────┘                │    │                └────┬─────┘  │
│       │                      │    │                     │        │
│       ▼                      │    │                     ▼        │
│  ┌─────────────┐  serialize │    │ deserialize  ┌─────────────┐│
│  │   Socket    │  Message   │    │   Message    │   Socket    ││
│  │   Sender    ├────────────┼────┼─────────────>│  Receiver   ││
│  │(port 5002)  │  TCP/IP    │    │  TCP/IP      │(port 5002)  ││
│  └─────────────┘            │    │              └─────────────┘│
│                              │    │                             │
│  ┌─────────────┐            │    │              ┌─────────────┐│
│  │   Socket    │<───────────┼────┼──────────────┤   Socket    ││
│  │  Receiver   │            │    │              │   Sender    ││
│  │(port 5001)  │            │    │              │(port 5001)  ││
│  └─────────────┘            │    │              └─────────────┘│
└──────────────────────────────┘    └──────────────────────────────┘
```

## Message Exchange Timeline

```
Time  Player1 (Initiator)           Player2 (Responder)
────  ──────────────────────────    ──────────────────────────
T0    counter=0, send "Hello" ───────────────> counter=0
T1                                              receive "Hello"
T2                               <───────────── send "Hello0", counter=1
T3    receive "Hello0", counter=1
T4    send "Hello01", counter=2 ───────────────> counter=1
T5                                              receive "Hello01"
T6                               <───────────── send "Hello012", counter=2
T7    receive "Hello012", counter=2
T8    send "Hello0123", counter=3 ──────────────> counter=2
...   (continues until Player1 counter=10)
```

## Message Content Evolution

```
Step  Sender    Message Content              Sender Counter
────  ────────  ───────────────────────────  ──────────────
1     Player1   "Hello"                      0 → 1
2     Player2   "Hello" + "0"                0 → 1
3     Player1   "Hello0" + "1"               1 → 2
4     Player2   "Hello01" + "2"              1 → 2
5     Player1   "Hello012" + "3"             2 → 3
6     Player2   "Hello0123" + "4"            2 → 3
7     Player1   "Hello01234" + "5"           3 → 4
8     Player2   "Hello012345" + "6"          3 → 4
9     Player1   "Hello0123456" + "7"         4 → 5
10    Player2   "Hello01234567" + "8"        4 → 5
11    Player1   "Hello012345678" + "9"       5 → 6
12    Player2   "Hello0123456789" + "0"      5 → 6
13    Player1   "Hello01234567890" + "1"     6 → 7
14    Player2   "Hello012345678901" + "2"    6 → 7
15    Player1   "Hello0123456789012" + "3"   7 → 8
16    Player2   "Hello01234567890123" + "4"  7 → 8
17    Player1   "Hello012345678901234" + "5" 8 → 9
18    Player2   "Hello0123456789012345" + "6" 8 → 9
19    Player1   "Hello01234567890123456" + "7" 9 → 10
```

At step 19, Player1 reaches counter=10 (sent 10 messages), triggering the stop condition.

## Stop Condition Logic

```java
// In both Main classes
while (player1.getSentMessageCount() < MAX_MESSAGES) {
    Thread.sleep(10);
}

// Stop condition met when player1.getSentMessageCount() == 10
```

**Result:**
- Player1 (initiator): 10 messages sent, 9 messages received
- Player2 (responder): 9 messages sent, 10 messages received (receives first)
