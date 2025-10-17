import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class DBUtil {
    private static final Dotenv dotenv = Dotenv.load();  // Load .env

    public static final String DB_URL = dotenv.get("DB_URL");
    public static final String DB_USER = dotenv.get("DB_USER");
    public static final String DB_PASS = dotenv.get("DB_PASS");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // JDBC driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}
