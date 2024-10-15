package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class LeaderBoard extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public LeaderBoard(BufferedReader in, PrintWriter out, String username, CardLayout cardLayout, Container container) {
        // Create a panel to hold components
        setLayout(new BorderLayout());

        // Create a label for the title
        JLabel titleLabel = new JLabel("Leaderboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Define column names for the table
        String[] columnNames = {"STT", "Name", "Score"};

        // Create the table model to hold data
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setEnabled(false); // Disable editing
        JScrollPane scrollPane = new JScrollPane(table); // Add scroll bar in case of many players
        add(scrollPane, BorderLayout.CENTER);


        // Nút quay lại Home
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> {
            cardLayout.show(container, "Home"); // Quay lại trang Home
        });
        add(backButton, BorderLayout.SOUTH);

        sendLeaderboardRequest(in, out);
    }

    private void sendLeaderboardRequest(BufferedReader in, PrintWriter out) {
        // Gửi yêu cầu tới server để lấy thông tin leaderboard
        out.println("LEADERBOARD");
        //System.out.println("da vao");
        // Tạo một luồng mới để lắng nghe phản hồi từ server
        new Thread(() -> {
            try {
            	 //System.out.println("da vao try");
                String response;
                while ((response = in.readLine()) != null) {
                	 //System.out.println("da vao while");
                	 //System.out.println(response);
                    if (response.startsWith("LEADERBOARD ")) {
                    	 //System.out.println("da vao if");
                        // Phân tích chuỗi dữ liệu nhận được
                        String[] data = response.split(" ");
                        
                        // Bắt đầu từ index 1 vì index 0 là từ khóa "LEADERBOARD"
                        for (int i = 1; i < data.length; i += 2) {
                        	 //System.out.println("da vao for");
                            String userName = data[i];
                            int score = Integer.parseInt(data[i + 1]);

                            // Thêm người chơi vào bảng
                            tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, userName, score});
                        }
                        break; // Dừng sau khi nhận được dữ liệu leaderboard
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    
}
