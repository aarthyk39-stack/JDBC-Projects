package hospitalmanagement;

import java.sql.*;
import java.util.Scanner;

public class DoctorDAO {

    public static void addDoctor(Scanner sc) {
        String name;
        do {
            System.out.print("Enter doctor name: ");
            name = sc.nextLine();
            if (!Validator.isValidName(name)) System.out.println("Invalid name!");
        } while (!Validator.isValidName(name));

        String specialization;
        do {
            System.out.print("Enter specialization: ");
            specialization = sc.nextLine();
            if (!Validator.isNotEmpty(specialization)) System.out.println("Cannot be empty!");
        } while (!Validator.isNotEmpty(specialization));

        String phone;
        do {
            System.out.print("Enter phone (10 digits): ");
            phone = sc.nextLine();
            if (!Validator.isValidPhone(phone)) System.out.println("Invalid phone!");
        } while (!Validator.isValidPhone(phone));

        String sql = "INSERT INTO doctors (name, specialization, phone) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setString(3, phone);
            int rows = ps.executeUpdate();
            System.out.println(rows + " doctor added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding doctor: " + e.getMessage());
        }
    }

    public static void viewDoctors() {
        String sql = "SELECT * FROM doctors";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n--- Doctor List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("doctor_id") + " | " + rs.getString("name") + " | "
                        + rs.getString("specialization") + " | " + rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching doctors: " + e.getMessage());
        }
    }

    public static Integer findDoctorId(Scanner sc, Connection conn) throws SQLException {
        String name;
        do {
            System.out.print("Enter doctor name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name)) System.out.println("Name cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String sql = "SELECT doctor_id, name, specialization FROM doctors WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            java.util.List<Integer> ids = new java.util.ArrayList<>();
            System.out.println("Matching doctors:");
            while (rs.next()) {
                ids.add(rs.getInt("doctor_id"));
                System.out.println(rs.getInt("doctor_id") + " | " + rs.getString("name") + " | " + rs.getString("specialization"));
            }
            if (ids.isEmpty()) { System.out.println("No doctor found with that name."); return null; }
            if (ids.size() == 1) return ids.get(0);

            int chosenId;
            do {
                System.out.print("Multiple matches.Enter correct doctor_id: ");
                String idInput = sc.nextLine();
                chosenId = Validator.isPositiveInteger(idInput) ? Integer.parseInt(idInput) : -1;
                if (!ids.contains(chosenId)) System.out.println("Invalid selection!");
            } while (!ids.contains(chosenId));
            return chosenId;
        }
    }
}