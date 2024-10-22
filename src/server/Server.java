package server;

import database.MySQLConnection;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static Map<String, ClientHandler> clients = new ConcurrentHashMap<>(); // Bản đồ lưu trữ ClientHandler

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
        private String userName;

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
                case "LOGOUT":
                    logoutUser(userName);  // Truyền username vào hàm logoutUser
                    break;
                case "VIEWSCORE":
                    sendUserScore(parts[1]); // Gọi phương thức để gửi điểm số về cho client
                    break;
                case "LEADERBOARD":
                    sendLeaderboard();
                    break;
                case "INVITE":
                    String invitedPlayer = parts[1];
                    checkPlayerStatus(invitedPlayer, userName);  // Kiểm tra trạng thái người chơi và xử lý lời mời
                    break;
                case "INVITE_ACCEPTED":
                    handleInviteAccepted(parts[1]); // Xử lý khi người chơi chấp nhận lời mời
                    break;
                case "INVITE_DECLINED":
                    handleInviteDeclined(parts[1]); // Xử lý khi người chơi từ chối lời mời
                    break;
                default:
                    out.println("Unknown command.");
            }
        }

        // kiểm tra người được mời có online hay không
        private void checkPlayerStatus(String invitedPlayer, String invitingPlayer) {
            String sql = "SELECT isActive FROM users WHERE userName = ?";
            
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, invitedPlayer);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    boolean isActive = rs.getBoolean("isActive");
                    
                    if (isActive) {
                        // Người chơi đang hoạt động, gửi lời mời
                        out.println("INVITE_SENT");
                        notifyInvitedPlayer(invitedPlayer, invitingPlayer);
                    } else {
                        // Người chơi không hoạt động
                        out.println("PLAYER_INACTIVE");
                    }
                } else {
                    out.println("ERROR: Player not found.");
                }
            } catch (SQLException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }

        // gửi thông báo lời mời chơi cho B
        private void notifyInvitedPlayer(String invitedPlayer, String invitingPlayer) {
            System.out.println("invite from dat");
            out.println("INVITE_FROM " + invitingPlayer);
        }

        // B chấp nhận lời mời
        private void handleInviteAccepted(String invitingPlayer) {
            notifyInviterAboutAcceptance(userName, invitingPlayer);
        }

        // B từ chối lời mời
        private void handleInviteDeclined(String invitingPlayer) {
            notifyInviterAboutDecline(userName, invitingPlayer);
        }

        // server phản hồi B chấp nhận A
        private void notifyInviterAboutAcceptance(String invitedPlayer, String invitingPlayer) {
            ClientHandler invitingHandler = clients.get(invitingPlayer);
            if (invitingHandler != null) {
                invitingHandler.out.println("INVITE_ACCEPTED " + invitedPlayer);
            }
        }

        // server phản hồi B từ chối A
        private void notifyInviterAboutDecline(String invitedPlayer, String invitingPlayer) {
            ClientHandler invitingHandler = clients.get(invitingPlayer);
            if (invitingHandler != null) {
                invitingHandler.out.println("INVITE_DECLINED " + invitedPlayer);
            }
        }
        //ok
        private void logoutUser(String username) {
            String sql = "UPDATE users SET isActive = 0 WHERE userName = ?";
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, username);
                pstmt.executeUpdate();
                clients.remove(username); // Xóa ClientHandler khỏi bản đồ
                out.println("LOGOUT_SUCCESS");
            } catch (SQLException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }
        // ok
        private void sendLeaderboard() {
            String sql = "SELECT userName, score FROM users ORDER BY score DESC LIMIT 10;";
            
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                StringBuilder leaderboardData = new StringBuilder("LEADERBOARD ");
                while (rs.next()) {
                    String userName = rs.getString("userName");
                    int score = rs.getInt("score");
                    leaderboardData.append(userName).append(" ").append(score).append(" ");
                }
                
                out.println(leaderboardData.toString().trim());
            } catch (SQLException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }
        // ok
        private void sendUserScore(String username) {
            String sql = "SELECT gamesPlayed, gamesWon, gamesLost, score FROM users WHERE userName = ?";
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int gamesPlayed = rs.getInt("gamesPlayed");
                    int gamesWon = rs.getInt("gamesWon");
                    int gamesLost = rs.getInt("gamesLost");
                    int totalScore = rs.getInt("score");
                    out.println("SCORE " + gamesPlayed + " " + gamesWon + " " + gamesLost + " " + totalScore);
                } else {
                    out.println("ERROR: User not found."); // Trường hợp không tìm thấy người dùng
                }
            } catch (SQLException e) {
                out.println("ERROR: " + e.getMessage());
            }
        }
        // ok
        private void registerUser(String username, String password) {
            String sql = "INSERT INTO users (userName, password) VALUES (?, ?)";
            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();
                out.println("REGISTER SUCCESS");
            } catch (SQLException e) {
                out.println("REGISTER FAILED: " + e.getMessage());
            }
        }
        // ok
        private void loginUser(String username, String password) {
            String sql = "SELECT * FROM users WHERE userName = ? AND password = ?";
            String updateSql = "UPDATE users SET isActive = 1 WHERE userName = ?";

            try (Connection conn = MySQLConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql);
                 PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {

                // Kiểm tra thông tin đăng nhập
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    this.userName = username; // Cập nhật biến userName
                    updatePstmt.setString(1, username);
                    updatePstmt.executeUpdate();
                    clients.put(username, this); // Thêm ClientHandler vào bản đồ
                    out.println("LOGIN SUCCESS: " + username);
                } else {
                    out.println("LOGIN FAILED: Invalid credentials.");
                }
            } catch (SQLException e) {
                out.println("LOGIN FAILED: " + e.getMessage());
            }
        }

        private ClientHandler findClientHandlerByUsername(String username) {
            return clients.get(username); // Tìm ClientHandler theo username
        }
    }
}
