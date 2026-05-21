package com.player;

/**
 * Contract for sending messages to recipients.
 * Responsibilities:
 * - Define abstraction for message delivery mechanism
 * - Support different implementations (in-process, inter-process)
 */
public interface MessageSender {
    void send(Message message, String recipientId);
}
