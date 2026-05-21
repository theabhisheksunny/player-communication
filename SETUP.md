# Setup Instructions

## Prerequisites

This project requires:
- **Java JDK 11 or higher** (for compiling and running)
- **Maven 3.6 or higher** (for building)

## Installing Java and Maven

### Ubuntu/Debian/WSL

```bash
sudo apt-get update
sudo apt-get install openjdk-11-jdk maven
```

Verify installation:
```bash
java -version
javac -version
mvn -version
```

### macOS (with Homebrew)

```bash
brew install openjdk@11 maven
```

### Windows

1. Download and install [Java JDK 11](https://adoptium.net/)
2. Download and install [Maven](https://maven.apache.org/download.cgi)
3. Add both to your PATH environment variable

## Building the Project

### Option 1: Using Maven (Recommended)

```bash
cd player-communication
mvn clean package
```

### Option 2: Manual Compilation

If Maven is not available:

```bash
cd player-communication
./compile-manual.sh
```

Or compile manually:

```bash
mkdir -p target/classes
javac -d target/classes src/main/java/com/player/*.java
```

## Running the Application

### Same Process Scenario

With Maven:
```bash
./run-same-process.sh
```

Or directly:
```bash
mvn exec:java -Dexec.mainClass="com.player.SameProcessMain"
```

With manual compilation:
```bash
java -cp target/classes com.player.SameProcessMain
```

### Different Processes Scenario

With Maven:
```bash
./run-different-processes.sh
```

Or manually in two terminals:

Terminal 1 (Player2 - Responder):
```bash
java -cp target/player-communication-1.0-SNAPSHOT.jar com.player.DifferentProcessMain Player2 5002 Player1 5001
```

Terminal 2 (Player1 - Initiator):
```bash
java -cp target/player-communication-1.0-SNAPSHOT.jar com.player.DifferentProcessMain Player1 5001 Player2 5002 initiator
```

Or with manual compilation:

Terminal 1:
```bash
java -cp target/classes com.player.DifferentProcessMain Player2 5002 Player1 5001
```

Terminal 2:
```bash
java -cp target/classes com.player.DifferentProcessMain Player1 5001 Player2 5002 initiator
```

## Troubleshooting

### "javac: command not found"
- Java JDK is not installed or not in PATH
- Install Java JDK as described above

### "JAVA_HOME environment variable is not defined"
Find your Java installation and set JAVA_HOME:

```bash
# On Linux/macOS
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# On Windows (Command Prompt)
set JAVA_HOME=C:\Program Files\Java\jdk-11
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Port already in use
If you get "Address already in use" error, the ports 5001 or 5002 are occupied. Either:
- Kill the process using those ports
- Change the ports in the run script or command line

### Socket connection refused
- Ensure the responder (Player2) is started before the initiator (Player1)
- Check that firewall allows connections on ports 5001 and 5002
