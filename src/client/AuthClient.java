package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.prefs.Preferences;

public class AuthClient extends JFrame {
    private static final int PORT = 12345; // Cổng server
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private CardLayout cardLayout;
    
    // Preferences để lưu thông tin người dùng
    private Preferences prefs;
    
    public AuthClient() {
        prefs = Preferences.userNodeForPackage(AuthClient.class);
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        setTitle("Game Authentication");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Trang Login
        JPanel loginPanel = createLoginPanel();
        add(loginPanel, "Login");

        // Trang Register
        JPanel registerPanel = createRegisterPanel();
        add(registerPanel, "Register");

        // Load saved credentials if available
        loadSavedCredentials();
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        
        // Checkbox nhớ mật khẩu
        JCheckBox rememberMeCheckBox = new JCheckBox("Remember Me");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login(usernameField.getText(), passwordField.getPassword(), rememberMeCheckBox.isSelected()));

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> cardLayout.show(getContentPane(), "Register"));

        // Panel cho nút Login và Register nằm cạnh nhau
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(rememberMeCheckBox); // Thêm checkbox vào panel
        panel.add(buttonPanel); // Thêm panel chứa nút vào panel chính

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            if (passwordField.getPassword().equals(confirmPasswordField.getPassword())) {
                register(usernameField.getText(), new String(passwordField.getPassword()));
            } else {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
            }
        });

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> cardLayout.show(getContentPane(), "Login"));

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(registerButton);
        panel.add(backButton);

        return panel;
    }

    private void login(String username, char[] password, boolean rememberMe) {
        String passwordStr = new String(password); // Chuyển đổi mảng ký tự sang String
        try {
            out.println("LOGIN " + username + " " + passwordStr);
            String response = in.readLine();
            if (response.startsWith("LOGIN SUCCESS")) {
                JOptionPane.showMessageDialog(this, response);
                // Tạo và hiển thị trang Home
                Home homePanel = new Home(username, cardLayout, getContentPane());
                add(homePanel, "Home");
                cardLayout.show(getContentPane(), "Home");

                // Nếu "Nhớ mật khẩu" được chọn, lưu thông tin đăng nhập
                if (rememberMe) {
                    saveCredentials(username, passwordStr);
                    saveRememberMeState(true); // Lưu trạng thái của checkbox
                } else {
                    clearCredentials();
                    saveRememberMeState(false); // Lưu trạng thái của checkbox
                }
            } else {
                JOptionPane.showMessageDialog(this, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void register(String username, String password) {
        try {
            out.println("REGISTER " + username + " " + password);
            String response = in.readLine();
            JOptionPane.showMessageDialog(this, response);
            if (response.startsWith("REGISTER SUCCESS")) {
                cardLayout.show(getContentPane(), "Login");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectToServer() {
        try {
            socket = new Socket("localhost", PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSavedCredentials() {
        String username = prefs.get("username", "");
        String password = prefs.get("password", "");
        
        // Lấy trạng thái của checkbox
        boolean rememberMe = prefs.getBoolean("rememberMe", false);

        // Lặp qua các thành phần để tìm JTextField và JPasswordField
        for (Component component : getContentPane().getComponents()) {
            if (component instanceof JPanel) {
                for (Component subComponent : ((JPanel) component).getComponents()) {
                    if (subComponent instanceof JTextField) {
                        ((JTextField) subComponent).setText(username);
                    } else if (subComponent instanceof JPasswordField) {
                        ((JPasswordField) subComponent).setText(password);
                    } else if (subComponent instanceof JCheckBox) {
                        ((JCheckBox) subComponent).setSelected(rememberMe); // Khôi phục trạng thái checkbox
                    }
                }
            }
        }
    }

    private void saveCredentials(String username, String password) {
        prefs.put("username", username); // Lưu tên đăng nhập
        prefs.put("password", password);  // Lưu mật khẩu
    }

    private void saveRememberMeState(boolean rememberMe) {
        prefs.putBoolean("rememberMe", rememberMe); // Lưu trạng thái của checkbox
    }

    private void clearCredentials() {
        prefs.remove("username");
        prefs.remove("password");
        prefs.remove("rememberMe"); // Xóa trạng thái của checkbox
    }

    public static void main(String[] args) {
        AuthClient client = new AuthClient();
        client.setVisible(true);
        client.connectToServer();
    }
}
