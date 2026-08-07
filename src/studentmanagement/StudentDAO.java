package studentmanagement;

import java.sql.*;
import java.util.Scanner;

public class StudentDAO {

    public static void addStudent(Scanner sc) {

        String name;
        do {
            System.out.print("Enter name: ");
            name = sc.nextLine();
            if (!Validator.isValidName(name))
                System.out.println("Invalid name! Only letters and spaces allowed.");
        } while (!Validator.isValidName(name));

        String email;
        do {
            System.out.print("Enter email: ");
            email = sc.nextLine();
            if (!Validator.isValidEmail(email))
                System.out.println("Invalid email format! Try again (e.g. name@example.com).");
        } while (!Validator.isValidEmail(email));

        String phone;
        do {
            System.out.print("Enter phone (10 digits): ");
            phone = sc.nextLine();
            if (!Validator.isValidPhone(phone))
                System.out.println("Invalid phone! Must be exactly 10 digits.");
        } while (!Validator.isValidPhone(phone));

        String sql = "INSERT INTO students (name, email, phone) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);

            int rows = ps.executeUpdate();
            System.out.println(rows + " student added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    public static void viewStudents() {
        String sql = "SELECT * FROM students";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Student List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("student_id") + " | "
                        + rs.getString("name") + " | "
                        + rs.getString("email") + " | "
                        + rs.getString("phone"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching students: " + e.getMessage());
        }
    }

    public static void updateStudent(Scanner sc) {

        String idInput;
        do {
            System.out.print("Enter student_id to update: ");
            idInput = sc.nextLine();
            if (!Validator.isPositiveInteger(idInput))
                System.out.println("Invalid ID! Must be a positive number.");
        } while (!Validator.isPositiveInteger(idInput));
        int id = Integer.parseInt(idInput);

        String email;
        do {
            System.out.print("Enter new email: ");
            email = sc.nextLine();
            if (!Validator.isValidEmail(email))
                System.out.println("Invalid email format! Try again.");
        } while (!Validator.isValidEmail(email));

        String phone;
        do {
            System.out.print("Enter new phone (10 digits): ");
            phone = sc.nextLine();
            if (!Validator.isValidPhone(phone))
                System.out.println("Invalid phone! Must be exactly 10 digits.");
        } while (!Validator.isValidPhone(phone));

        String sql = "UPDATE students SET email = ?, phone = ? WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setInt(3, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Student updated!" : "No student found with that ID.");

        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    public static void deleteStudent(Scanner sc) {
        String idInput;
        do {
            System.out.print("Enter student_id to delete: ");
            idInput = sc.nextLine();
            if (!Validator.isPositiveInteger(idInput))
                System.out.println("Invalid ID! Must be a positive number.");
        } while (!Validator.isPositiveInteger(idInput));
        int studentId = Integer.parseInt(idInput);

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String deleteMarksSql = """
            DELETE m FROM marks m
            JOIN enrollments e ON m.enrollment_id = e.enrollment_id
            WHERE e.student_id = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(deleteMarksSql)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            String deleteAttendanceSql = """
            DELETE a FROM attendance a
            JOIN enrollments e ON a.enrollment_id = e.enrollment_id
            WHERE e.student_id = ?
            """;
            try (PreparedStatement ps = conn.prepareStatement(deleteAttendanceSql)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            String deleteEnrollmentsSql = "DELETE FROM enrollments WHERE student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteEnrollmentsSql)) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            String deleteStudentSql = "DELETE FROM students WHERE student_id = ?";
            int rows;
            try (PreparedStatement ps = conn.prepareStatement(deleteStudentSql)) {
                ps.setInt(1, studentId);
                rows = ps.executeUpdate();
            }

            if (rows > 0) {
                conn.commit();   // all 4 steps succeeded -> make it permanent
                System.out.println("Student and all related records deleted successfully!");
            } else {
                conn.rollback();  // no such student -> undo everything (nothing to undo here, but safe practice)
                System.out.println("No student found with that ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting student. Rolling back all changes...");
            try {
                if (conn != null) conn.rollback();   // ANY step failed -> undo all 4 steps, nothing gets half-deleted
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            System.out.println("Details: " + e.getMessage());

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);  // reset to normal mode
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}