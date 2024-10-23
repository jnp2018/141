package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class LeaderBoard extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    // Constructor mới nhận dữ liệu leaderboard từ Home
    public LeaderBoard(String[][] leaderboardData, CardLayout cardLayout, Container container) {
        // Tạo giao diện cho Leaderboard
        setLayout(new BorderLayout());

        // Tạo tiêu đề cho bảng xếp hạng
        JLabel titleLabel = new JLabel("Leaderboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Định nghĩa cột cho bảng
        String[] columnNames = {"STT", "Name", "Score"};

        // Tạo mô hình bảng với dữ liệu
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setEnabled(false); // Disable editing
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Điền dữ liệu vào bảng từ mảng 2 chiều
        for (int i = 0; i < leaderboardData.length; i++) {
            tableModel.addRow(new Object[]{i + 1, leaderboardData[i][0], leaderboardData[i][1]});
        }

        // Nút quay lại Home
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> {
            cardLayout.show(container, "Home"); // Quay lại trang Home
        });
        add(backButton, BorderLayout.SOUTH);
    }
}
