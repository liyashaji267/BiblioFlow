// Book.java
public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String edition;
    private double price;
    private int stockQuantity;
    private String rackNumber;
    private String location;
    private String imagePath;
    private String genre;
    
    public Book(int id, String isbn, String title, String author, String publisher, 
                String edition, double price, int stockQuantity, String rackNumber, 
                String location, String imagePath, String genre) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.edition = edition;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.rackNumber = rackNumber;
        this.location = location;
        this.imagePath = imagePath;
        this.genre = genre;
    }
    
    // Getters and setters
    public int getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public String getEdition() { return edition; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public String getRackNumber() { return rackNumber; }
    public String getLocation() { return location; }
    public String getImagePath() { return imagePath; }
    public String getGenre() { return genre; }
}