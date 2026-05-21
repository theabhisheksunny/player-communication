package com.player;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of MessageSender for inter-process communication using sockets.
 * Responsibilities:
 * - Maintain mapping of player IDs to network addresses (host:port)
 * - Serialize and send messages over TCP sockets
 * - Handle connection establishment and cleanup
 * - Manage communication errors
 */
public class SocketMessageSender implements MessageSender {
    private final Map<String, PlayerAddress> addressMap;

    public SocketMessageSender() {
        this.addressMap = new ConcurrentHashMap<>();
    }

    public void registerPlayerAddress(String playerId, String host, int port) {
        addressMap.put(playerId, new PlayerAddress(host, port));
    }

    @Override
    public void send(Message message, String recipientId) {
        PlayerAddress address = addressMap.get(recipientId);
        if (address == null) {
            System.err.println("No address found for recipient: " + recipientId);
            return;
        }

        try (Socket socket = new Socket(address.host, address.port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Failed to send message to " + recipientId + ": " + e.getMessage());
        }
    }

    private static class PlayerAddress {
        final String host;
        final int port;

        PlayerAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
