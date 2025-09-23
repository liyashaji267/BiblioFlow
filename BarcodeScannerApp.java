product.getPrice()
            };
            tableModel.addRow(row);
            log("Added: " + product.getName());
        } else {
            log("Product not found for barcode: " + barcode);
        }
    }
    
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BarcodeScannerApp().setVisible(true);
            }
        });
    }
    
    // Product class
    class Product {
        private String barcode;
        private String name;
        private double price;
        
        public Product(String barcode, String name, double price) {
            this.barcode = barcode;
            this.name = name;
            this.price = price;
        }
        
        public String getBarcode() { return barcode; }
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
}