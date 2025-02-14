package server.services.user_service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.User;

public interface UserService extends Remote {
    User login(String username, String password) throws RemoteException;
    boolean register(User user) throws RemoteException;
}
