package client;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;

public class Home extends JPanel {
    private CardLayout cardLayout;
    private Container container;
    private PrintWriter out;
    private BufferedReader in; // To receive data from server
    private String username; // Store username to identify the player

    public Home(String username, CardLayout cardLayout, Container container, PrintWriter out, BufferedReader in) {
        this.username = username; // Store the username
        this.cardLayout = cardLayout;
        this.container = container;
        this.out = out;
        this.in = in;

        setLayout(new BorderLayout());

        JLabel greetingLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        add(greetingLabel, BorderLayout.NORTH);

        JButton inviteButton = new JButton("Invite Players");
        inviteButton.addActionListener(e -> showInviteDialog());
        add(inviteButton, BorderLayout.WEST);

        JButton viewScoreButton = new JButton("View Score");
        viewScoreButton.addActionListener(e -> {
            out.println("VIEWSCORE " + username);  // Send request to view score
        });
        add(viewScoreButton, BorderLayout.CENTER);

        JButton leaderboardButton = new JButton("View Leaderboard");
        leaderboardButton.addActionListener(e -> {
            out.println("LEADERBOARD");  // Send request to view leaderboard
        });
        add(leaderboardButton, BorderLayout.EAST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            out.println("LOGOUT " + username);  // Send request to logout
        });
        add(logoutButton, BorderLayout.SOUTH);

        // Single thread to listen to all server responses
        new Thread(this::listenToServer).start();
    }

    // Method to listen to server responses in a single thread
    private void listenToServer() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                String[] parts = response.split(" ");
                switch (parts[0]) {
                    case "INVITE_FROM":
                        handleInvite(parts[1]);
                        break;
                    case "SCORE":
                        handleScore(parts);
                        break;
                    case "LEADERBOARD":
                        handleLeaderboard(parts);
                        break;
                    case "LOGOUT_SUCCESS":
                        SwingUtilities.invokeLater(() -> cardLayout.show(container, "Login"));
                        break;
                    case "INVITE_ACCEPTED":
                        notifyInviteAccepted(parts[1]);
                        // Redirect to the game if the other player has accepted
                        checkIfBothPlayersAccepted(parts[1]);
                        break;
                    case "INVITE_DECLINED":
                        notifyInviteDeclined(parts[1]);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInviteDialog() {
        JDialog inviteDialog = new JDialog((Frame) null, "Invite Player", true);
        inviteDialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2, 1));
        JLabel label = new JLabel("Enter player username to invite:");
        JTextField playerNameField = new JTextField(20);
        panel.add(label);
        panel.add(playerNameField);

        JPanel buttonPanel = new JPanel();
        JButton sendButton = new JButton("Send Invite");
        JButton cancelButton = new JButton("Cancel");

        sendButton.addActionListener(event -> {
            String invitedPlayer = playerNameField.getText();
            if (!invitedPlayer.isEmpty()) {
                out.println("INVITE " + invitedPlayer);  // Send invite request
            } else {
                JOptionPane.showMessageDialog(inviteDialog, "Please enter a username.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(event -> inviteDialog.dispose());
        sendButton.addActionListener(event -> inviteDialog.dispose());
        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        inviteDialog.add(panel, BorderLayout.CENTER);
        inviteDialog.add(buttonPanel, BorderLayout.SOUTH);
        inviteDialog.setSize(300, 150);
        inviteDialog.setLocationRelativeTo(null);
        inviteDialog.setVisible(true);
    }

    private void handleInvite(String invitingPlayer) {
        SwingUtilities.invokeLater(() -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    invitingPlayer + " has invited you to a game. Do you accept?",
                    "Game Invitation", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                out.println("INVITE_ACCEPTED " + invitingPlayer);
                JOptionPane.showMessageDialog(this, "You accepted the invitation. Please wait for the game to start.");
                // Chuyển sang màn hình Game cho B
               // cardLayout.show(container, "Game");
            } else {
                out.println("INVITE_DECLINED " + invitingPlayer);
                JOptionPane.showMessageDialog(this, "You declined the invitation.");
            }
        });
    }


    private void handleScore(String[] data) {
        int gamesPlayed = Integer.parseInt(data[1]);
        int gamesWon = Integer.parseInt(data[2]);
        int gamesLost = Integer.parseInt(data[3]);
        int score = Integer.parseInt(data[4]);

        SwingUtilities.invokeLater(() -> {
            ViewScore viewScorePanel = new ViewScore(gamesPlayed, gamesWon, gamesLost, score, cardLayout, container);
            container.add(viewScorePanel, "ViewScore");
            cardLayout.show(container, "ViewScore");
        });
    }

    private void handleLeaderboard(String[] data) {
        String[][] leaderboardData = new String[(data.length - 1) / 2][2];
        for (int i = 1, index = 0; i < data.length; i += 2, index++) {
            leaderboardData[index][0] = data[i];
            leaderboardData[index][1] = data[i + 1];
        }

        SwingUtilities.invokeLater(() -> {
            LeaderBoard leaderBoardPanel = new LeaderBoard(leaderboardData, cardLayout, container);
            container.add(leaderBoardPanel, "LeaderBoard");
            cardLayout.show(container, "LeaderBoard");
        });
    }

    private void notifyInviteAccepted(String invitedPlayer) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, invitedPlayer + " accepted your invite, wait a moment to start the game");
            // Chuyển sang màn hình Game cho A
            //cardLayout.show(container, "Game");
        });
    }


    private void notifyInviteDeclined(String invitedPlayer) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, invitedPlayer + " did not accept your invite"));
    }

    // Check if both players have accepted the invitation
    private void checkIfBothPlayersAccepted(String invitedPlayer) {
        // Notify both players and redirect to the Game page
        SwingUtilities.invokeLater(() -> {
            // Assuming you have a Game class to handle the game logic
            Game gamePanel = new Game(username, invitedPlayer);
            container.add(gamePanel, "Game");
            cardLayout.show(container, "Game");
        });
    }
}
