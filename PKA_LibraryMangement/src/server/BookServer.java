package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.services.NotificationService;
import server.services.book_service.BookServiceImpl;

public class BookServer {
    private NotificationService notificationService;

    public BookServer () {
        notificationService = new NotificationService();
    }
    public static void main(String[] args) {
        BookServer bookServer = new BookServer();
        try {
            BookServiceImpl bookServiceImpl = new BookServiceImpl(bookServer.notificationService);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("BookService", bookServiceImpl);

            System.err.println("Book Server ready");
        } catch (Exception e) {
            System.err.println("Book Server exception: " + e.toString());
            e.printStackTrace();
            bookServer.close();
        }
    }

    public void close() {
        if (notificationService != null) {
            notificationService.close();
        }
    }
}
