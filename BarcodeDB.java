import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;

public class BarcodeDB {
    private static final Dotenv dotenv = Dotenv.load();  // Load .env

    public static final String BARCODE_URL = dotenv.get("BARCODE_URL");
    public static final String BARCODE_USER = dotenv.get("BARCODE_USER");
    public static final String BARCODE_PASS = dotenv.get("BARCODE_PASS");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // JDBC driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(BARCODE_URL, BARCODE_USER, BARCODE_PASS);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Connected to BarcodeDetails_db successfully!");
        } catch (SQLException e) {
            System.out.println("DB connection failed: " + e.getMessage());
        }
    }
}
