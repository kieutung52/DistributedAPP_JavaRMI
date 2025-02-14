package view;

import model.User;
import javax.swing.*;
import java.awt.*;
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

    public Client() {
        setTitle("Library Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        try {
            Registry registryUser = LocateRegistry.getRegistry("localhost", 1100);
            userService = (UserService) registryUser.lookup("UserService");

            Registry registryBook = LocateRegistry.getRegistry("localhost", 1099);
            bookService = (BookService) registryBook.lookup("BookService");

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
}