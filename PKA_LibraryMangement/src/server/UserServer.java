package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.services.user_service.UserServiceImpl;

public class UserServer {
    public static void main(String[] args) {
        try {
            UserServiceImpl userServiceImpl = new UserServiceImpl();
            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.createRegistry(1100);
            registry.bind("UserService", userServiceImpl);

            System.err.println("User Server ready");
        } catch (Exception e) {
            System.err.println("User Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
