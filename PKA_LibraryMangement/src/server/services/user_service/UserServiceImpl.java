package server.services.user_service;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

import model.User;

public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    private Connection connection;

    public UserServiceImpl() throws RemoteException, SQLException {
        String url = "jdbc:mysql://mysql-c1071a-kieuthanhtung0502-6dee.c.aivencloud.com:18485/PKA_libraryRMI?useSSL=false";
        String user = "avnadmin";
        String password = "AVNS_NZCUzPfcnm3RVyLqLtB";
        connection = DriverManager.getConnection(url, user, password);
    }

    @Override
    public User login(String username, String password) throws RemoteException {
        try {
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("role")
                );
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi đăng nhập: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean register(User user) throws RemoteException {
        try {
            String sql = "INSERT INTO user (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Lỗi đăng ký: " + e.getMessage());
        }
    }
}
