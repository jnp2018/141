package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLServerConnection {

    // Hàm tạo kết nối tới SQL Server, định nghĩa là public
    public static Connection getConnection() throws SQLException {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"
                             + "databaseName=Caro;"
                             + "user=sa;" // Thay bằng username SQL Server của bạn
                             + "password=1234567;" // Thay bằng password SQL Server của bạn
                             + "encrypt=true;"
                             + "trustServerCertificate=true;";

        return DriverManager.getConnection(connectionUrl);
    }

    public static void main(String[] args) {
        // Kiểm tra kết nối
        try (Connection conn = SQLServerConnection.getConnection()) {
            if (conn != null) {
                System.out.println("Connected to SQL Server successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
