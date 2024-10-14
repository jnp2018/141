package client;

import javax.swing.*;
import java.awt.*;

public class Home extends JPanel {
    public Home(String username, CardLayout cardLayout, Container container) {
        setLayout(new BorderLayout());

        JLabel greetingLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        add(greetingLabel, BorderLayout.NORTH);

        JButton inviteButton = new JButton("Invite Players");
        add(inviteButton, BorderLayout.WEST);

        JButton leaderboardButton = new JButton("View Leaderboard");
        add(leaderboardButton, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> cardLayout.show(container, "Login"));
        add(logoutButton, BorderLayout.SOUTH);
    }
}
