package server;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class AuthServer {
    private static final int PORT = 12345; // Cổng mà server sẽ lắng nghe
    private static HashMap<String, String> users = new HashMap<>(); // Lưu trữ người dùng và mật khẩu

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Chấp nhận kết nối từ client
                new Thread(new ClientHandler(clientSocket)).start(); // Tạo một thread mới để xử lý client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Lớp xử lý client
    static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] parts = inputLine.split(" ");
                    String command = parts[0]; // Lệnh đăng ký hoặc đăng nhập
                    String username = parts[1]; // Tên người dùng
                    String password = parts[2]; // Mật khẩu

                    if (command.equals("REGISTER")) {
                        registerUser(username, password, out);
                    } else if (command.equals("LOGIN")) {
                        loginUser(username, password, out);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void registerUser(String username, String password, PrintWriter out) {
            if (users.containsKey(username)) {
                out.println("REGISTER FAILED: Tên người dùng đã tồn tại.");
            } else {
                users.put(username, password);
                out.println("REGISTER SUCCESS: Tài khoản đã được tạo.");
            }
        }

        private void loginUser(String username, String password, PrintWriter out) {
            if (users.containsKey(username) && users.get(username).equals(password)) {
                out.println("LOGIN SUCCESS: Đăng nhập thành công.");
            } else {
                out.println("LOGIN FAILED: Tên người dùng hoặc mật khẩu không chính xác.");
            }
        }
    }
}
