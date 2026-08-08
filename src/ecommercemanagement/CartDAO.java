package ecommercemanagement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CartDAO {

    private static List<CartItem> cart = new ArrayList<>();

    public static void addToCart(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer productId = ProductDAO.findProductId(sc, conn);
            if (productId == null) return;

            int availableStock = ProductDAO.getStock(productId, conn);
            if (availableStock <= 0) {
                System.out.println("Sorry, this product is out of stock!");
                return;
            }

            String qtyInput;
            int qty=0;
            do {
                System.out.print("Enter quantity: ");
                qtyInput = sc.nextLine();
                if (!Validator.isPositiveInteger(qtyInput)) {
                    System.out.println("Invalid quantity!");
                    continue;
                }
                qty = Integer.parseInt(qtyInput);
                if (qty > availableStock) {
                    System.out.println("Only " + availableStock + " in stock. Enter a smaller quantity.");
                }
            } while (!Validator.isPositiveInteger(qtyInput) || qty > availableStock);

            double price = ProductDAO.getPrice(productId, conn);

            String sql = "SELECT product_name FROM products WHERE product_id = ?";
            String productName;
            try (var ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                var rs = ps.executeQuery();
                rs.next();
                productName = rs.getString("product_name");
            }

            cart.add(new CartItem(productId, productName, qty, price));
            System.out.println(productName + " x " + qty + " added to cart!");

        } catch (SQLException e) {
            System.out.println("Error adding to cart: " + e.getMessage());
        }
    }

    public static void viewCart() {
        if (cart.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("\n===== Your Cart =====");
        double total = 0;
        for (CartItem item : cart) {
            System.out.printf("%-25s Qty: %-5d Price: ₹%-10.2f Subtotal: ₹%.2f%n",
                    item.productName, item.quantity, item.price, item.getSubtotal());
            total += item.getSubtotal();
        }
        System.out.printf("Total: ₹%.2f%n", total);
    }

    public static List<CartItem> getCart() {
        return cart;
    }

    public static void clearCart() {
        cart.clear();
    }
}