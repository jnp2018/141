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
                default:
                    out.println("Unknown command.");
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
