package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {

    public static Connection getConnection() throws SQLException {
        String connectionUrl = "jdbc:mysql://localhost:3306/GameCaro?useSSL=false&serverTimezone=UTC";
        String user = "root"; // Thay bằng username MySQL của bạn
        String password = "ngocanhnek1"; // Thay bằng password MySQL của bạn

        return DriverManager.getConnection(connectionUrl, user, password);
    }

    public static void main(String[] args) {
        try (Connection conn = MySQLConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Connected to MySQL successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
