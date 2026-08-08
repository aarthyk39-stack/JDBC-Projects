package ecommercemanagement;

import java.sql.*;
import java.util.Scanner;

public class CustomerDAO {

    public static void addCustomer(Scanner sc) {
        String name;
        do {
            System.out.print("Enter customer name: ");
            name = sc.nextLine();
            if (!Validator.isValidName(name)) System.out.println("Invalid name!");
        } while (!Validator.isValidName(name));

        String email;
        do {
            System.out.print("Enter email: ");
            email = sc.nextLine();
            if (!Validator.isValidEmail(email)) System.out.println("Invalid email!");
        } while (!Validator.isValidEmail(email));

        String phone;
        do {
            System.out.print("Enter phone (10 digits): ");
            phone = sc.nextLine();
            if (!Validator.isValidPhone(phone)) System.out.println("Invalid phone!");
        } while (!Validator.isValidPhone(phone));

        System.out.print("Enter address: ");
        String address = sc.nextLine();

        String sql = "INSERT INTO customers (name, email, phone, address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, address);
            int rows = ps.executeUpdate();
            System.out.println(rows + " customer registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    public static void viewCustomers() {
        String sql = "SELECT * FROM customers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n--- Customer List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("customer_id") + " | " + rs.getString("name") + " | "
                        + rs.getString("email") + " | " + rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching customers: " + e.getMessage());
        }
    }

    public static Integer findCustomerId(Scanner sc, Connection conn) throws SQLException {
        String name;
        do {
            System.out.print("Enter your name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name)) System.out.println("Cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String sql = "SELECT customer_id, name, email FROM customers WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            java.util.List<Integer> ids = new java.util.ArrayList<>();
            System.out.println("Matching customers:");
            while (rs.next()) {
                ids.add(rs.getInt("customer_id"));
                System.out.println(rs.getInt("customer_id") + " | " + rs.getString("name") + " | " + rs.getString("email"));
            }
            if (ids.isEmpty()) { System.out.println("No customer found. Please register first."); return null; }
            if (ids.size() == 1) return ids.get(0);

            int chosenId;
            do {
                System.out.print("Multiple matches. Enter correct customer_id: ");
                String idInput = sc.nextLine();
                chosenId = Validator.isPositiveInteger(idInput) ? Integer.parseInt(idInput) : -1;
                if (!ids.contains(chosenId)) System.out.println("Invalid selection!");
            } while (!ids.contains(chosenId));
            return chosenId;
        }
    }
}