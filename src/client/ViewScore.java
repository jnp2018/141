package client;

import javax.swing.*;
import java.awt.*;

public class ViewScore extends JPanel {
    private JLabel gamesPlayedLabel;
    private JLabel gamesWonLabel;
    private JLabel gamesLostLabel;
    private JLabel scoreLabel;
    private CardLayout cardLayout; // CardLayout
    private Container container;    // Container

    // Constructor mới nhận các tham số điểm số từ Home
    public ViewScore(int gamesPlayed, int gamesWon, int gamesLost, int score, CardLayout cardLayout, Container container) {
        this.cardLayout = cardLayout;
        this.container = container;

        setLayout(new GridLayout(7, 1, 10, 10));
        
        JLabel titleLabel = new JLabel("Personal Score", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);

        gamesPlayedLabel = new JLabel("Games Played: " + gamesPlayed, SwingConstants.CENTER);
        add(gamesPlayedLabel);

        gamesWonLabel = new JLabel("Games Won: " + gamesWon, SwingConstants.CENTER);
        add(gamesWonLabel);

        gamesLostLabel = new JLabel("Games Lost: " + gamesLost, SwingConstants.CENTER);
        add(gamesLostLabel);

        scoreLabel = new JLabel("Total Score: " + score, SwingConstants.CENTER);
        add(scoreLabel);

        // Nút Back
        JButton backButton = new JButton("Back To Home");
        backButton.addActionListener(e -> cardLayout.show(container, "Home")); // Chuyển về Home
        add(backButton);
    }
}
