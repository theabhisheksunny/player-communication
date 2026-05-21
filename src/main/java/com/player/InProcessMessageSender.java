package com.player;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Implementation of MessageSender for in-process communication.
 * Responsibilities:
 * - Maintain registry of all message handlers (players) by ID
 * - Route messages asynchronously to avoid stack overflow from recursive calls
 * - Thread-safe message delivery via message queue
 */
public class InProcessMessageSender implements MessageSender {
    private final Map<String, MessageHandler> handlers;
    private final BlockingQueue<MessageEnvelope> messageQueue;
    private final Thread deliveryThread;
    private volatile boolean running;

    public InProcessMessageSender() {
        this.handlers = new ConcurrentHashMap<>();
        this.messageQueue = new LinkedBlockingQueue<>();
        this.running = true;
        this.deliveryThread = new Thread(this::deliverMessages);
        this.deliveryThread.start();
    }

    public void registerHandler(String id, MessageHandler handler) {
        handlers.put(id, handler);
    }

    @Override
    public void send(Message message, String recipientId) {
        messageQueue.offer(new MessageEnvelope(message, recipientId));
    }

    private void deliverMessages() {
        while (running) {
            try {
                MessageEnvelope envelope = messageQueue.take();
                MessageHandler handler = handlers.get(envelope.recipientId);
                if (handler != null) {
                    handler.onMessageReceived(envelope.message);
                } else {
                    System.err.println("No handler found for recipient: " + envelope.recipientId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stop() {
        running = false;
        deliveryThread.interrupt();
    }

    private static class MessageEnvelope {
        final Message message;
        final String recipientId;

        MessageEnvelope(Message message, String recipientId) {
            this.message = message;
            this.recipientId = recipientId;
        }
    }
}
