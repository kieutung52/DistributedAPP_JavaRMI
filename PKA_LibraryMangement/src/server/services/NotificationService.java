package server.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService {

    private static final int NOTIFICATION_PORT = 2000;
    private List<PrintWriter> clientWriters = new ArrayList<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public NotificationService() {
        startServer();
    }

    private void startServer() {
        executorService.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(NOTIFICATION_PORT)) {
                System.out.println("Notification Server started on port " + NOTIFICATION_PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected to Notification Server");
                    PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                    clientWriters.add(writer);
                    // No need for a separate thread to *listen* to clients.  We only *send*.
                }
            } catch (IOException e) {
                System.err.println("Error in Notification Server: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            try {
                writer.println(message);
            } catch (Exception e) {
                System.err.println("Error sending message to client: " + e.getMessage());
            }
        }
    }

    public void close() {
        executorService.shutdownNow();
    }
}