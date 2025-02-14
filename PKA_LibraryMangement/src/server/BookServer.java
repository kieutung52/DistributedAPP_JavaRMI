package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.services.book_service.BookServiceImpl;

public class BookServer {
    public static void main(String[] args) {
        try {
            BookServiceImpl bookServiceImpl = new BookServiceImpl();
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("BookService", bookServiceImpl);

            System.err.println("Book Server ready");
        } catch (Exception e) {
            System.err.println("Book Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
