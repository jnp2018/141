package client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ViewScore extends JPanel {
    private JLabel gamesPlayedLabel;
    private JLabel gamesWonLabel;
    private JLabel gamesLostLabel;
    private JLabel scoreLabel;
    private PrintWriter out;
    private BufferedReader in; // Thêm biến BufferedReader
    private String username;
    private CardLayout cardLayout; // Thêm CardLayout
    private Container container;    // Thêm Container

    public ViewScore(BufferedReader in, PrintWriter out, String username, CardLayout cardLayout, Container container) {
        this.in = in; // Khởi tạo BufferedReader
        this.out = out;
        this.username = username;
        this.cardLayout = cardLayout; // Khởi tạo CardLayout
        this.container = container;    // Khởi tạo Container

        setLayout(new GridLayout(6, 1, 10, 10));

        JLabel titleLabel = new JLabel("Personal Score", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel);

        gamesPlayedLabel = new JLabel("Games Played: ", SwingConstants.CENTER);
        add(gamesPlayedLabel);

        gamesWonLabel = new JLabel("Games Won: ", SwingConstants.CENTER);
        add(gamesWonLabel);

        gamesLostLabel = new JLabel("Games Lost: ", SwingConstants.CENTER);
        add(gamesLostLabel);

        scoreLabel = new JLabel("Total Score: ", SwingConstants.CENTER);
        add(scoreLabel);

        // Tạo nút Back
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(container, "Home")); // Chuyển về Home
        add(backButton);

        // Gửi yêu cầu tới server để lấy điểm số
        sendUserScore(out, username);
        System.out.println(username);
    }

    private void sendUserScore(PrintWriter out, String username) {
        // Gửi yêu cầu tới server để lấy thông tin điểm số
        out.println("VIEWSCORE " + username);
System.out.println("da vao ham");
        // Lắng nghe phản hồi từ server
        new Thread(() -> {
            try {
            	System.out.println("da vao try");
                String response;
                while ((response = in.readLine()) != null) {
                	System.out.println("da vao while");
                    if (response.startsWith("SCORE ")) {
                    	System.out.println("da vao if");
                        String[] data = response.split(" ");
                        int gamesPlayed = Integer.parseInt(data[1]);
                        int gamesWon = Integer.parseInt(data[2]);
                        int gamesLost = Integer.parseInt(data[3]);
                        int score = Integer.parseInt(data[4]);

                        // Cập nhật dữ liệu hiển thị
                        gamesPlayedLabel.setText("Games Played: " + gamesPlayed);
                        gamesWonLabel.setText("Games Won: " + gamesWon);
                        gamesLostLabel.setText("Games Lost: " + gamesLost);
                        scoreLabel.setText("Total Score: " + score);
                        System.out.println("ok" + gamesPlayed+ gamesWon+ gamesLost + score);
                        break; // Đã nhận được dữ liệu, không cần tiếp tục nghe
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
