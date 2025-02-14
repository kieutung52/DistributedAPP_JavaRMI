package view;

import model.User;
import server.services.user_service.UserService; 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Authentication extends JPanel {
    private Client client;
    private UserService userService;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Authentication(Client client) {
        this.client = client;
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1100); 
            userService = (UserService) registry.lookup("UserService"); 
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối đến User Server.");
            System.exit(0);
        }
        setLayout(new GridBagLayout());

        
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Đăng nhập");
        JButton registerButton = new JButton("Đăng ký");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(registerButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim(); 
                String password = new String(passwordField.getPassword());
        
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(Authentication.this, "Vui lòng nhập tên đăng nhập.");
                    return;
                }
        
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(Authentication.this, "Vui lòng nhập mật khẩu.");
                    return;
                }
        
                try {
                    User user = userService.login(username, password);
                    if (user != null) {
                        client.setCurrentUser(user);
                    } else {
                        JOptionPane.showMessageDialog(Authentication.this, "Tên đăng nhập hoặc mật khẩu không chính xác."); 
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Authentication.this, "Lỗi đăng nhập: " + ex.getMessage()); 
                }
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
        
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(Authentication.this, "Vui lòng nhập tên đăng nhập.");
                    return;
                }
        
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(Authentication.this, "Vui lòng nhập mật khẩu.");
                    return;
                }
        
                User newUser = new User(username, password, "user");
                try {
                    if (userService.register(newUser)) {
                        JOptionPane.showMessageDialog(Authentication.this, "Đăng ký thành công.");
                    } else {
                        JOptionPane.showMessageDialog(Authentication.this, "Đăng ký thất bại. Tên đăng nhập có thể đã tồn tại.");
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(Authentication.this, "Lỗi đăng ký: " + ex.getMessage());
                }
            }
        });

        setVisible(true);
    }
}