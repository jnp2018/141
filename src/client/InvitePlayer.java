package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class InvitePlayer extends JPanel {
    
    private JTextField playerNameField;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private CardLayout cardLayout;
    private Container container;

    public InvitePlayer(BufferedReader in, PrintWriter out, String username, CardLayout cardLayout, Container container2) {
        this.in = in;
        this.out = out;
        this.username = username;
        this.cardLayout = cardLayout;
        this.container = container2;

        setLayout(new BorderLayout());

        // Tạo giao diện nhập tên người chơi
        JLabel label = new JLabel("Enter player username to invite:");
        playerNameField = new JTextField(20);
        JButton inviteButton = new JButton("Send Invite");
        JButton backButton = new JButton("Back");

        // Hành động khi nhấn "Send Invite"
        inviteButton.addActionListener(e -> {
            String invitedPlayer = playerNameField.getText();
            sendInviteRequest(invitedPlayer);
        });

        // Hành động khi nhấn "Back"
        backButton.addActionListener(e -> {
            // Quay lại trang chủ
            cardLayout.show(container2, "Home");
        });

        // Tạo panel chứa các thành phần giao diện
        JPanel panel = new JPanel();
        panel.add(label);
        panel.add(playerNameField);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(inviteButton);
        buttonPanel.add(backButton);

        // Thêm các thành phần vào giao diện chính
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Hàm gửi yêu cầu mời người chơi đến server
    private void sendInviteRequest(String invitedPlayer) {
        // Gửi yêu cầu tới server
        out.println("INVITE " + invitedPlayer);

        // Xử lý phản hồi từ server (có thể xử lý sau nếu cần)
        new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    if (response.equals("PLAYER_INACTIVE")) {
                        JOptionPane.showMessageDialog(this, invitedPlayer + " is not active.");
                        break;
                    } else if (response.equals("INVITE_SENT")) {
                        JOptionPane.showMessageDialog(this, "Invitation sent to " + invitedPlayer + ". Please wait for confirmation.");
                        // Quay lại trang chủ sau khi nhấn OK
                        SwingUtilities.invokeLater(() -> cardLayout.show(container, "Home"));
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
