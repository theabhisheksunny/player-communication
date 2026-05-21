package com.player;

/**
 * Main entry point for same-process player communication.
 * Responsibilities:
 * - Initialize two players within the same JVM process
 * - Set up in-process message routing
 * - Initiate message exchange
 * - Monitor stop condition (initiator sent and received 10 messages)
 * - Gracefully terminate when stop condition is met
 */
public class SameProcessMain {
    private static final int MAX_MESSAGES = 10;

    public static void main(String[] args) {
        System.out.println("=== Starting Same Process Communication ===\n");

        InProcessMessageSender messageSender = new InProcessMessageSender();

        Player player1 = new Player("Player1", messageSender);
        Player player2 = new Player("Player2", messageSender);

        player1.setMaxMessages(MAX_MESSAGES);

        messageSender.registerHandler("Player1", player1);
        messageSender.registerHandler("Player2", player2);

        System.out.println("Player1 (initiator) and Player2 created in PID: " + ProcessHandle.current().pid());
        System.out.println("Starting message exchange...\n");

        player1.sendMessage("Hello", "Player2");

        while (player1.getSentMessageCount() < MAX_MESSAGES) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        player1.deactivate();
        player2.deactivate();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n=== Stop Condition Met ===");
        System.out.println("Player1 sent: " + player1.getSentMessageCount() + " messages");
        System.out.println("Player2 sent: " + player2.getSentMessageCount() + " messages");
        System.out.println("Terminating gracefully.");

        messageSender.stop();
    }
}
