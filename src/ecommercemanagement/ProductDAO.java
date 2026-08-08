package ecommercemanagement;

import java.sql.*;
import java.util.Scanner;

public class ProductDAO {

    public static void addProduct(Scanner sc) {
        String name;
        do {
            System.out.print("Enter product name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name)) System.out.println("Cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String priceInput;
        do {
            System.out.print("Enter price: ");
            priceInput = sc.nextLine();
            if (!Validator.isPositiveDecimal(priceInput)) System.out.println("Invalid price!");
        } while (!Validator.isPositiveDecimal(priceInput));

        String stockInput;
        do {
            System.out.print("Enter stock quantity: ");
            stockInput = sc.nextLine();
            if (!Validator.isPositiveInteger(stockInput)) System.out.println("Invalid quantity!");
        } while (!Validator.isPositiveInteger(stockInput));

        String sql = "INSERT INTO products (product_name, price, stock_quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, Double.parseDouble(priceInput));
            ps.setInt(3, Integer.parseInt(stockInput));
            int rows = ps.executeUpdate();
            System.out.println(rows + " product added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    public static void viewCatalog() {
        String sql = "SELECT * FROM products WHERE stock_quantity > 0";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n===== Product Catalog =====");
            System.out.printf("%-5s %-25s %-10s %-10s%n", "ID", "Name", "Price", "In Stock");
            while (rs.next()) {
                System.out.printf("%-5d %-25s %-10.2f %-10d%n",
                        rs.getInt("product_id"), rs.getString("product_name"),
                        rs.getDouble("price"), rs.getInt("stock_quantity"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching catalog: " + e.getMessage());
        }
    }

    public static Integer findProductId(Scanner sc, Connection conn) throws SQLException {
        String name;
        do {
            System.out.print("Enter product name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name)) System.out.println("Cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String sql = "SELECT product_id, product_name, price, stock_quantity FROM products WHERE product_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            java.util.List<Integer> ids = new java.util.ArrayList<>();
            System.out.println("Matching products:");
            while (rs.next()) {
                ids.add(rs.getInt("product_id"));
                System.out.printf("%d | %s | ₹%.2f | Stock: %d%n", rs.getInt("product_id"),
                        rs.getString("product_name"), rs.getDouble("price"), rs.getInt("stock_quantity"));
            }
            if (ids.isEmpty()) { System.out.println("No product found."); return null; }
            if (ids.size() == 1) return ids.get(0);

            int chosenId;
            do {
                System.out.print("Multiple matches. Enter correct product_id: ");
                String idInput = sc.nextLine();
                chosenId = Validator.isPositiveInteger(idInput) ? Integer.parseInt(idInput) : -1;
                if (!ids.contains(chosenId)) System.out.println("Invalid selection!");
            } while (!ids.contains(chosenId));
            return chosenId;
        }
    }

    public static double getPrice(int productId, Connection conn) throws SQLException {
        String sql = "SELECT price FROM products WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("price") : 0;
        }
    }

    public static int getStock(int productId, Connection conn) throws SQLException {
        String sql = "SELECT stock_quantity FROM products WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("stock_quantity") : 0;
        }
    }
}