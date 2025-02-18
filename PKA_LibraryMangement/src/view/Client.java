package view;

import model.User;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import server.services.book_service.BookService;
import server.services.user_service.UserService;

public class Client extends JFrame {
    private UserService userService;
    private BookService bookService;
    private CardLayout cardLayout;
    private Authentication authenticationPanel;
    private UserPanel userPanel;
    private AdminPanel adminPanel;
    private User currentUser;
    private static final int NOTIFICATION_PORT = 2000; 
    private Socket notificationSocket;
    private BufferedReader notificationReader;

    public Client() {
        setTitle("Library Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        try {
            Registry registryUser = LocateRegistry.getRegistry("localhost", 1100);
            userService = (UserService) registryUser.lookup("UserService");

            Registry registryBook = LocateRegistry.getRegistry("localhost", 1099);
            bookService = (BookService) registryBook.lookup("BookService");

            startNotificationListener(); 

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối đến server.");
            System.exit(0);
        }

        cardLayout = new CardLayout();
        setLayout(cardLayout);

        authenticationPanel = new Authentication(this);
        userPanel = new UserPanel(this);
        adminPanel = new AdminPanel(this);

        add(authenticationPanel, "authentication");
        add(userPanel, "user");
        add(adminPanel, "admin");

        showPanel("authentication");

        setVisible(true);
    }

    private void startNotificationListener() {
        new Thread(() -> {
            try {
                notificationSocket = new Socket("localhost", NOTIFICATION_PORT);
                notificationReader = new BufferedReader(new InputStreamReader(notificationSocket.getInputStream()));
                String message;
                while ((message = notificationReader.readLine()) != null) {
                    if ("update".equals(message)) {
                        SwingUtilities.invokeLater(() -> {
                            
                            if (currentUser != null) {
                                if ("admin".equals(currentUser.getRole())) {
                                    adminPanel.viewAllBooks();
                                    adminPanel.viewAllRequests();
                                } else {
                                    userPanel.viewAllBooks();
                                    userPanel.updateBorrowRequests();
                                }
                            }
                        });
                    }
                }
            } catch (IOException e) {
                System.err.println("Notification connection error: " + e.getMessage());
            } finally {
                closeNotificationConnection(); 
            }
        }).start();
    }
    private void closeNotificationConnection() {
        try {
            if (notificationReader != null) {
                notificationReader.close();
            }
            if (notificationSocket != null) {
                notificationSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing notification connection: " + e.getMessage());
        }
    }

    public void showPanel(String panelName) {
        cardLayout.show(this.getContentPane(), panelName);
    }


    public void setCurrentUser(User user) {
        this.currentUser = user;
        if (user != null) {
            if (user.getRole().equals("admin")) {
                if (adminPanel != null) {
                    adminPanel.setCurrentUser(user);
                    showPanel("admin");
                }
            } else {
                if (userPanel != null) {
                    userPanel.setCurrentUser(user);
                    showPanel("user");
                }
            }
        } else {
            showPanel("authentication");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public BookService getBookService() {
        return bookService;
    }

    public UserService getUserService() {
        return userService;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client());
    }
    
     public void close() {
        try {
            if(notificationReader != null){
                notificationReader.close();
            }
            if(notificationSocket != null) {
                notificationSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}