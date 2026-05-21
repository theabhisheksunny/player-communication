package com.player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Listens for incoming messages over TCP socket.
 * Responsibilities:
 * - Accept incoming socket connections on a specific port
 * - Deserialize received messages
 * - Deliver messages to the registered message handler
 * - Run in a separate thread to avoid blocking
 * - Support graceful shutdown
 */
public class SocketMessageReceiver implements Runnable {
    private final int port;
    private final MessageHandler handler;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public SocketMessageReceiver(int port, MessageHandler handler) {
        this.port = port;
        this.handler = handler;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleConnection(clientSocket);
                } catch (SocketException e) {
                    if (!running) {
                        break;
                    }
                    System.err.println("Socket error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to start server on port " + port + ": " + e.getMessage());
        } finally {
            closeServerSocket();
        }
    }

    private void handleConnection(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            Message message = (Message) in.readObject();
            handler.onMessageReceived(message);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error receiving message: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        closeServerSocket();
    }

    private void closeServerSocket() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket: " + e.getMessage());
            }
        }
    }
}
