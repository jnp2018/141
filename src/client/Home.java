package client;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.BufferedReader;

public class Home extends JPanel {
    private CardLayout cardLayout;
    private Container container;
    private PrintWriter out;
    private BufferedReader in; // Để nhận dữ liệu từ server

    public Home(String username, CardLayout cardLayout, Container container, PrintWriter out, BufferedReader in) {
        this.cardLayout = cardLayout;
        this.container = container;
        this.out = out;
        this.in = in;

        setLayout(new BorderLayout());

        JLabel greetingLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        add(greetingLabel, BorderLayout.NORTH);

        JButton inviteButton = new JButton("Invite Players");
        add(inviteButton, BorderLayout.WEST);

        JButton viewScoreButton = new JButton("View Score");
        viewScoreButton.addActionListener(e -> {
            // Gửi yêu cầu đến server để lấy điểm số
            out.println("VIEWSCORE " + username);

            // Chuyển sang trang ViewScore
            ViewScore viewScorePanel = new ViewScore(in, out, username, cardLayout, container);
            container.add(viewScorePanel, "ViewScore");
            cardLayout.show(container, "ViewScore");
        });
        add(viewScoreButton, BorderLayout.CENTER);

        JButton leaderboardButton = new JButton("View Leaderboard");
        leaderboardButton.addActionListener(e -> {
            // Gửi yêu cầu đến server để lấy thông tin leaderboard
            out.println("LEADERBOARD ");

            // Chuyển sang trang Leaderboard
            LeaderBoard leaderBoardPanel = new LeaderBoard(in, out, username, cardLayout, container);
            container.add(leaderBoardPanel, "LeaderBoard");
            cardLayout.show(container, "LeaderBoard");
        });
        add(leaderboardButton, BorderLayout.EAST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> cardLayout.show(container, "Login"));
        add(logoutButton, BorderLayout.SOUTH);
    }
}
