package ecommercemanagement;

public class CartItem {
    int productId;
    String productName;
    int quantity;
    double price;

    public CartItem(int productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public double getSubtotal() {
        return quantity * price;
    }
}