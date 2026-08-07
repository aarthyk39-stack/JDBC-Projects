package hospitalmanagement;

import java.sql.*;
import java.util.Scanner;

public class PatientDAO {

    public static void addPatient(Scanner sc) {
        String name;
        do {
            System.out.print("Enter patient name: ");
            name = sc.nextLine();
            if (!Validator.isValidName(name)) System.out.println("Invalid name!");
        } while (!Validator.isValidName(name));

        String ageInput;
        do {
            System.out.print("Enter age: ");
            ageInput = sc.nextLine();
            if (!Validator.isPositiveInteger(ageInput)) System.out.println("Invalid age!");
        } while (!Validator.isPositiveInteger(ageInput));
        int age = Integer.parseInt(ageInput);

        String gender;
        do {
            System.out.print("Enter gender (Male/Female/Other): ");
            gender = sc.nextLine();
            if (!Validator.isValidGender(gender)) System.out.println("Invalid gender!");
        } while (!Validator.isValidGender(gender));

        String phone;
        do {
            System.out.print("Enter phone (10 digits): ");
            phone = sc.nextLine();
            if (!Validator.isValidPhone(phone)) System.out.println("Invalid phone!");
        } while (!Validator.isValidPhone(phone));

        System.out.print("Enter address: ");
        String address = sc.nextLine();

        String sql = "INSERT INTO patients (name, age, gender, phone, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, address);
            int rows = ps.executeUpdate();
            System.out.println(rows + " patient added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding patient: " + e.getMessage());
        }
    }

    public static void viewPatients() {
        String sql = "SELECT * FROM patients";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n--- Patient List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("patient_id") + " | " + rs.getString("name") + " | "
                        + rs.getInt("age") + " | " + rs.getString("gender") + " | "
                        + rs.getString("phone") + " | " + rs.getString("address"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching patients: " + e.getMessage());
        }
    }

    public static Integer findPatientId(Scanner sc, Connection conn) throws SQLException {
        String name;
        do {
            System.out.print("Enter patient name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name)) System.out.println("Name cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String sql = "SELECT patient_id, name, phone FROM patients WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            java.util.List<Integer> ids = new java.util.ArrayList<>();
            System.out.println("Matching patients:");
            while (rs.next()) {
                ids.add(rs.getInt("patient_id"));
                System.out.println(rs.getInt("patient_id") + " | " + rs.getString("name") + " | " + rs.getString("phone"));
            }
            if (ids.isEmpty()) { System.out.println("No patient found with that name."); return null; }
            if (ids.size() == 1) return ids.get(0);

            int chosenId;
            do {
                System.out.print("Multiple matches. Enter correct patient_id: ");
                String idInput = sc.nextLine();
                chosenId = Validator.isPositiveInteger(idInput) ? Integer.parseInt(idInput) : -1;
                if (!ids.contains(chosenId)) System.out.println("Invalid selection!");
            } while (!ids.contains(chosenId));
            return chosenId;
        }
    }

    public static void deletePatient(Scanner sc) {
        String idInput;
        do {
            System.out.print("Enter patient_id to delete: ");
            idInput = sc.nextLine();
            if (!Validator.isPositiveInteger(idInput)) System.out.println("Invalid ID!");
        } while (!Validator.isPositiveInteger(idInput));
        int patientId = Integer.parseInt(idInput);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);   // start transaction

            String deletePrescriptionsSql = """
            DELETE pr FROM prescriptions pr
            JOIN appointments a ON pr.appointment_id = a.appointment_id
            WHERE a.patient_id = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(deletePrescriptionsSql)) {
                ps.setInt(1, patientId);
                ps.executeUpdate();
            }

            String deleteAppointmentsSql = "DELETE FROM appointments WHERE patient_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteAppointmentsSql)) {
                ps.setInt(1, patientId);
                ps.executeUpdate();
            }

            String deletePatientSql = "DELETE FROM patients WHERE patient_id = ?";
            int rows;
            try (PreparedStatement ps = conn.prepareStatement(deletePatientSql)) {
                ps.setInt(1, patientId);
                rows = ps.executeUpdate();
            }

            if (rows > 0) {
                conn.commit();
                System.out.println("Patient and all related records deleted successfully!");
            } else {
                conn.rollback();
                System.out.println("No patient found with that ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting patient. Rolling back all changes...");
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
}