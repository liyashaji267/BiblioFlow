public class Book {
    private int id;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String edition;
    private double price;
    private int stockQuantity;   // Available stock in DB
    private String rackNumber;
    private String location;
    private String imagePath;
    private String genre;


    // Quantity the customer wants to buy
    private int cartQuantity;

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
        this.cartQuantity = 0; // default, set when added to cart
    }

    // ==========================
    // Getters
    // ==========================
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
    public int getCartQuantity() { return cartQuantity; }

    // ==========================
    // Setters
    // ==========================
    public void setId(int id) { this.id = id; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setEdition(String edition) { this.edition = edition; }
    public void setPrice(double price) { this.price = price; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public void setRackNumber(String rackNumber) { this.rackNumber = rackNumber; }
    public void setLocation(String location) { this.location = location; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setCartQuantity(int cartQuantity) { this.cartQuantity = cartQuantity; }
}