package server;

import database.SQLServerConnection;

import java.io.*;
import java.net.*;
import java.sql.*;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                String request;
                while ((request = in.readLine()) != null) {
                    handleRequest(request);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleRequest(String request) {
            String[] parts = request.split(" ");
            String command = parts[0];

            switch (command) {
                case "REGISTER":
                    registerUser(parts[1], parts[2]);
                    break;
                case "LOGIN":
                    loginUser(parts[1], parts[2]);
                    break;
                case "VIEWSCORE":
                    sendUserScore(parts[1]); // Gọi phương thức để gửi điểm số về cho client
                    break;
                case "LEADERBOARD":
                    sendLeaderboard();
                    break;
                default:
                    out.println("Unknown command.");
            }
        }

        private void sendLeaderboard() {
            String sql = "SELECT TOP 10 userName, score FROM Caro.dbo.Users ORDER BY score DESC;";
            
            try (Connection conn = SQLServerConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
            	 System.out.println("da vao try");
                
                StringBuilder leaderboardData = new StringBuilder("LEADERBOARD ");
                while (rs.next()) {
                	 System.out.println("da vao while");
                    String userName = rs.getString("userName");
                    int score = rs.getInt("score");
                    leaderboardData.append(userName).append(" ").append(score).append(" ");
                }
                
                // Trim the trailing space and send the leaderboard data back to the client
                out.println(leaderboardData.toString().trim());
                
            } catch (SQLException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }

		private void sendUserScore(String username) {
            String sql = "SELECT gamesPlayed, gamesWon, gamesLost, score FROM Caro.dbo.Users WHERE userName = ?";
            //System.out.println("goi xong sql");
            try (Connection conn = SQLServerConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //System.out.println("try");
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                	//System.out.println("da vao if");
                    int gamesPlayed = rs.getInt("gamesPlayed");
                    int gamesWon = rs.getInt("gamesWon");
                    int gamesLost = rs.getInt("gamesLost");
                    int totalScore = rs.getInt("score");
                    //System.out.println(gamesPlayed );
                    // Gửi kết quả về client
                    out.println("SCORE " + gamesPlayed + " " + gamesWon + " " + gamesLost + " " + totalScore);
                } else {
                    out.println("ERROR: User not found."); // Trường hợp không tìm thấy người dùng
                }
            } catch (SQLException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }

        
        private void registerUser(String username, String password) {
            String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
            try (Connection conn = SQLServerConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
                out.println("REGISTER SUCCESS");
            } catch (SQLException e) {
                out.println("REGISTER FAILED: " + e.getMessage());
            }
        }

        private void loginUser(String username, String password) {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
            try (Connection conn = SQLServerConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    out.println("LOGIN SUCCESS: " + username);
                } else {
                    out.println("LOGIN FAILED: Invalid credentials.");
                }
            } catch (SQLException e) {
                out.println("LOGIN FAILED: " + e.getMessage());
            }
        }
    }
}
