package com.player;

/**
 * Contract for handling received messages.
 * Responsibilities:
 * - Define callback interface for message reception
 * - Enable loose coupling between communication mechanism and message processing
 */
public interface MessageHandler {
    void onMessageReceived(Message message);
}
