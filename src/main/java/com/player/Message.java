package com.player;

import java.io.Serializable;

/**
 * Represents a message exchanged between players.
 * Responsibilities:
 * - Encapsulate message content
 * - Track message sender
 * - Support serialization for inter-process communication
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String content;
    private final String senderId;

    public Message(String content, String senderId) {
        this.content = content;
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public String getSenderId() {
        return senderId;
    }

    @Override
    public String toString() {
        return "Message{from='" + senderId + "', content='" + content + "'}";
    }
}
