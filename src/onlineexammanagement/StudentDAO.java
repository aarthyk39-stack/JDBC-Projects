package onlineexammanagement;

import java.sql.*;
import java.util.Scanner;

public class StudentDAO {

    public static void registerStudent(Scanner sc) {
        String name;
        do {
            System.out.print("Enter name: ");
            name = sc.nextLine();
            if (!Validator.isValidName(name)) System.out.println("Invalid name!");
        } while (!Validator.isValidName(name));

        String email;
        do {
            System.out.print("Enter email: ");
            email = sc.nextLine();
            if (!Validator.isValidEmail(email)) System.out.println("Invalid email!");
        } while (!Validator.isValidEmail(email));

        String password;
        do {
            System.out.print("Create password (min 6 characters): ");
            password = sc.nextLine();
            if (!Validator.isValidPassword(password)) System.out.println("Password too short!");
        } while (!Validator.isValidPassword(password));

        String phone;
        do {
            System.out.print("Enter phone (10 digits): ");
            phone = sc.nextLine();
            if (!Validator.isValidPhone(phone)) System.out.println("Invalid phone!");
        } while (!Validator.isValidPhone(phone));

        String sql = "INSERT INTO students (name, email, password, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, phone);
            int rows = ps.executeUpdate();
            System.out.println(rows + " student registered successfully! You can now login.");
        } catch (SQLException e) {
            System.out.println("Error registering student (email/phone may already exist): " + e.getMessage());
        }
    }

    public static Integer loginStudent(Scanner sc) {
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        String sql = "SELECT student_id, name FROM students WHERE email = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Login successful! Welcome, " + rs.getString("name"));
                return rs.getInt("student_id");
            } else {
                System.out.println("Invalid email or password.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }
}