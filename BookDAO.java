// BookDAO.java
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, publisher, edition, price, stock_quantity, rack_number, location, image_path, genre) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthor());
            stmt.setString(4, book.getPublisher());
            stmt.setString(5, book.getEdition());
            stmt.setDouble(6, book.getPrice());
            stmt.setInt(7, book.getStockQuantity());
            stmt.setString(8, book.getRackNumber());
            stmt.setString(9, book.getLocation());
            stmt.setString(10, book.getImagePath());
            stmt.setString(11, book.getGenre());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public Book findBookByISBN(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Book(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("publisher"),
                    rs.getString("edition"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("rack_number"),
                    rs.getString("location"),
                    rs.getString("image_path"),
                    rs.getString("genre")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("id"),
                    rs.getString("isbn"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("publisher"),
                    rs.getString("edition"),
                    rs.getDouble("price"),
                    rs.getInt("stock_quantity"),
                    rs.getString("rack_number"),
                    rs.getString("location"),
                    rs.getString("image_path"),
                    rs.getString("genre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getBooksFromSubstores() {
    List<Book> books = new ArrayList<>();
    String sql = "SELECT * FROM books WHERE location <> 'Main Store'";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Book book = new Book(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher"),
                rs.getString("edition"),
                rs.getDouble("price"),
                rs.getInt("stock_quantity"),
                rs.getString("rack_number"),
                rs.getString("location"),   // use this instead of storeName
                rs.getString("image_path"), // use this instead of subStoreName
                rs.getString("genre")
            );
            books.add(book);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return books;
}

public List<Book> searchBooks(String query, String column) {
    List<Book> books = new ArrayList<>();
    String sql = "SELECT * FROM books WHERE " + column + " LIKE ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, "%" + query + "%");
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            books.add(new Book(
                rs.getInt("id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("publisher"),
                rs.getString("edition"),
                rs.getDouble("price"),
                rs.getInt("stock_quantity"),
                rs.getString("rack_number"),
                rs.getString("location"),
                rs.getString("image_path"),
                rs.getString("genre")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return books;
}


}