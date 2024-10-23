package client;

import javax.swing.*;
import java.awt.*;

public class Game extends JPanel {
    private String playerA;
    private String playerB;

    public Game(String playerA, String playerB) {
        this.playerA = playerA;
        this.playerB = playerB;

        // Set up the game panel layout
        setLayout(new BorderLayout());

        // Example of adding components to the game panel
        JLabel gameLabel = new JLabel("Game started between " + playerA + " and " + playerB, SwingConstants.CENTER);
        add(gameLabel, BorderLayout.NORTH);

        // Here you can add your game board or other components
        // For example:
        JPanel gameBoard = new JPanel();
        gameBoard.setBackground(Color.LIGHT_GRAY); // Just an example
        add(gameBoard, BorderLayout.CENTER);
    }
}
