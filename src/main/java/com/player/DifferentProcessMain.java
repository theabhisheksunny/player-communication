package com.player;

/**
 * Main entry point for different-process player communication.
 * Responsibilities:
 * - Initialize a single player in its own JVM process
 * - Set up socket-based message sending and receiving
 * - For initiator: initiate message exchange and monitor stop condition
 * - For responder: continuously listen for messages
 * - Accept command-line arguments to configure player role and network settings
 * - Gracefully terminate when appropriate
 */
public class DifferentProcessMain {
    private static final int MAX_MESSAGES = 10;

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 4) {
            System.err.println("Usage: java DifferentProcessMain <playerId> <localPort> <remotePlayerId> <remotePort> [initiator]");
            System.err.println("Example: java DifferentProcessMain Player1 5001 Player2 5002 initiator");
            System.exit(1);
        }

        String playerId = args[0];
        int localPort = Integer.parseInt(args[1]);
        String remotePlayerId = args[2];
        int remotePort = Integer.parseInt(args[3]);
        boolean isInitiator = args.length > 4 && "initiator".equalsIgnoreCase(args[4]);

        System.out.println("=== Starting " + playerId + " in separate process ===");
        System.out.println("PID: " + ProcessHandle.current().pid());
        System.out.println("Local port: " + localPort);
        System.out.println("Remote player: " + remotePlayerId + " on port " + remotePort);
        System.out.println("Role: " + (isInitiator ? "Initiator" : "Responder") + "\n");

        SocketMessageSender messageSender = new SocketMessageSender();
        messageSender.registerPlayerAddress(remotePlayerId, "localhost", remotePort);

        Player player = new Player(playerId, messageSender);

        if (isInitiator) {
            player.setMaxMessages(MAX_MESSAGES);
        }

        SocketMessageReceiver receiver = new SocketMessageReceiver(localPort, player);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();

        Thread.sleep(1000);

        if (isInitiator) {
            System.out.println("Initiating message exchange...\n");
            player.sendMessage("Hello", remotePlayerId);

            while (player.getSentMessageCount() < MAX_MESSAGES) {
                Thread.sleep(10);
            }

            System.out.println("\n=== Stop Condition Met ===");
            System.out.println(playerId + " sent: " + player.getSentMessageCount() + " messages");
            System.out.println("Terminating gracefully.");

            receiver.stop();
            receiverThread.join(1000);
        } else {
            System.out.println("Waiting for messages...\n");
            receiverThread.join();
        }
    }
}
