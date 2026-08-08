package ecommercemanagement;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

public class OrderDAO {

    public static void placeOrder(Scanner sc) {
        List<CartItem> cart = CartDAO.getCart();

        if (cart.isEmpty()) {
            System.out.println("Your cart is empty! Add items before placing an order.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // start transaction

            Integer customerId = CustomerDAO.findCustomerId(sc, conn);
            if (customerId == null) { conn.rollback(); return; }

            // Step 1: check stock is still enough for every item before doing anything
            for (CartItem item : cart) {
                int currentStock = ProductDAO.getStock(item.productId, conn);
                if (item.quantity > currentStock) {
                    System.out.println("Sorry, " + item.productName + " only has " + currentStock + " left. Order cancelled.");
                    conn.rollback();
                    return;
                }
            }

            double totalAmount = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

            String orderSql = "INSERT INTO orders (customer_id, order_date, total_amount, status) VALUES (?, CURDATE(), ?, 'Placed')";
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setDouble(2, totalAmount);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                orderId = keys.getInt(1);
            }

            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(itemSql)) {
                for (CartItem item : cart) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.productId);
                    ps.setInt(3, item.quantity);
                    ps.setDouble(4, item.price);
                    ps.addBatch();   // queue this insert instead of running immediately
                }
                ps.executeBatch();
            }

            String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateStockSql)) {
                for (CartItem item : cart) {
                    ps.setInt(1, item.quantity);
                    ps.setInt(2, item.productId);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            System.out.printf("Order placed successfully! Order ID: %d | Total: ₹%.2f%n", orderId, totalAmount);
            CartDAO.clearCart();

        } catch (SQLException e) {
            System.out.println("Error placing order. Rolling back...");
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            System.out.println("Details: " + e.getMessage());

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void trackOrders(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer customerId = CustomerDAO.findCustomerId(sc, conn);
            if (customerId == null) return;

            String sql = "SELECT order_id, order_date, total_amount, status FROM orders WHERE customer_id = ? ORDER BY order_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                ResultSet rs = ps.executeQuery();

                System.out.println("\n===== Your Orders =====");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf("Order #%d | %s | ₹%.2f | Status: %s%n",
                            rs.getInt("order_id"), rs.getDate("order_date"),
                            rs.getDouble("total_amount"), rs.getString("status"));
                }
                if (!found) System.out.println("No orders found.");
            }

        } catch (SQLException e) {
            System.out.println("Error tracking orders: " + e.getMessage());
        }
    }

    public static void viewOrderDetails(Scanner sc) {
        System.out.print("Enter order_id: ");
        String idInput = sc.nextLine();
        if (!Validator.isPositiveInteger(idInput)) {
            System.out.println("Invalid order ID!");
            return;
        }
        int orderId = Integer.parseInt(idInput);

        String sql = """
            SELECT p.product_name, oi.quantity, oi.price_at_purchase
            FROM order_items oi
            JOIN products p ON oi.product_id = p.product_id
            WHERE oi.order_id = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- Order #" + orderId + " Details ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-25s Qty: %-5d Price: ₹%.2f%n",
                        rs.getString("product_name"), rs.getInt("quantity"), rs.getDouble("price_at_purchase"));
            }
            if (!found) System.out.println("No such order found.");

        } catch (SQLException e) {
            System.out.println("Error fetching order details: " + e.getMessage());
        }
    }

    public static void updateOrderStatus(Scanner sc) {
        System.out.print("Enter order_id: ");
        String idInput = sc.nextLine();
        if (!Validator.isPositiveInteger(idInput)) { System.out.println("Invalid ID!"); return; }
        int orderId = Integer.parseInt(idInput);

        System.out.print("Enter new status (Placed/Shipped/Delivered/Cancelled): ");
        String status = sc.nextLine();

        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Status updated!" : "No order found.");
        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
        }
    }
}