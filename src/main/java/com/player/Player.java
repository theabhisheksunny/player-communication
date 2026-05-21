package com.player;

/**
 * Represents a player that can send and receive messages.
 * Responsibilities:
 * - Maintain player identity
 * - Track sent message count
 * - Process incoming messages and generate responses
 * - Delegate actual message sending to a MessageSender
 */
public class Player implements MessageHandler {
    private final String id;
    private final MessageSender messageSender;
    private int sentMessageCount;
    private volatile boolean active;
    private int maxMessages;

    public Player(String id, MessageSender messageSender) {
        this.id = id;
        this.messageSender = messageSender;
        this.sentMessageCount = 0;
        this.active = true;
        this.maxMessages = Integer.MAX_VALUE;
    }

    public String getId() {
        return id;
    }

    public int getSentMessageCount() {
        return sentMessageCount;
    }

    public void sendMessage(String content, String recipientId) {
        Message message = new Message(content, this.id);
        messageSender.send(message, recipientId);
        sentMessageCount++;
        System.out.println("[" + id + "] Sent message #" + sentMessageCount + ": " + content);
    }

    @Override
    public void onMessageReceived(Message message) {
        System.out.println("[" + id + "] Received: " + message.getContent());

        if (active && sentMessageCount < maxMessages) {
            int counterToAppend = sentMessageCount;
            String responseContent = message.getContent() + counterToAppend;
            sendMessage(responseContent, message.getSenderId());
        }
    }

    public void deactivate() {
        this.active = false;
    }

    public void setMaxMessages(int max) {
        this.maxMessages = max;
    }
}
