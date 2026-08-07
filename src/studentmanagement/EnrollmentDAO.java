package studentmanagement;

import java.sql.*;
import java.util.Scanner;

public class EnrollmentDAO {


    public static Integer findStudentId(Scanner sc, Connection conn) throws SQLException {

    String name;
        do {
            System.out.print("Enter student name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name))
                System.out.println("Name cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String sql = "SELECT student_id, name, email FROM students WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            java.util.List<Integer> ids = new java.util.ArrayList<>();
            System.out.println("Matching students:");
            while (rs.next()) {
                ids.add(rs.getInt("student_id"));
                System.out.println(rs.getInt("student_id") + " | "
                        + rs.getString("name") + " | " + rs.getString("email"));
            }

            if (ids.isEmpty()) {
                System.out.println("No student found with that name.");
                return null;
            }
            if (ids.size() == 1) return ids.get(0);

            int chosenId;
            do {
                System.out.print("Multiple matches found. Enter the correct student_id from above: ");
                String idInput = sc.nextLine();
                chosenId = Validator.isPositiveInteger(idInput) ? Integer.parseInt(idInput) : -1;
                if (!ids.contains(chosenId))
                    System.out.println("Invalid selection! Pick an ID from the list shown.");
            } while (!ids.contains(chosenId));

            return chosenId;
        }
    }

    public static Integer findEnrollmentId(Scanner sc, Connection conn) throws SQLException {
        Integer studentId = findStudentId(sc, conn);
        if (studentId == null) return null;

        Integer courseId = findCourseId(sc, conn);
        if (courseId == null) return null;

        String sql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("enrollment_id");
            } else {
                System.out.println("This student is not enrolled in that course!");
                return null;
            }
        }
    }

    public static Integer findCourseId(Scanner sc, Connection conn) throws SQLException {
        String name;
        do {
            System.out.print("Enter course name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name))
                System.out.println("Name cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String sql = "SELECT course_id, course_name, duration_months FROM courses WHERE course_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            java.util.List<Integer> ids = new java.util.ArrayList<>();
            System.out.println("Matching courses:");
            while (rs.next()) {
                ids.add(rs.getInt("course_id"));
                System.out.println(rs.getInt("course_id") + " | "
                        + rs.getString("course_name") + " | "
                        + rs.getInt("duration_months") + " months");
            }

            if (ids.isEmpty()) {
                System.out.println("No course found with that name.");
                return null;
            }
            if (ids.size() == 1) return ids.get(0);

            int chosenId;
            do {
                System.out.print("Multiple matches found. Enter the correct course_id from above: ");
                String idInput = sc.nextLine();
                chosenId = Validator.isPositiveInteger(idInput) ? Integer.parseInt(idInput) : -1;
                if (!ids.contains(chosenId))
                    System.out.println("Invalid selection! Pick an ID from the list shown.");
            } while (!ids.contains(chosenId));

            return chosenId;
        }
    }

    public static void enrollStudent(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer studentId = findStudentId(sc, conn);
            if (studentId == null) return;

            Integer courseId = findCourseId(sc, conn);
            if (courseId == null) return;

            String date;
            do {
                System.out.print("Enter enrollment date (yyyy-MM-dd): ");
                date = sc.nextLine();
                if (!Validator.isValidDate(date))
                    System.out.println("Invalid date format! Use yyyy-MM-dd.");
            } while (!Validator.isValidDate(date));

            String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                ps.setDate(3, Date.valueOf(date));

                int rows = ps.executeUpdate();
                System.out.println(rows + " enrollment added successfully!");
            }

        } catch (SQLException e) {
            // duplicate enrollment will fail here due to UNIQUE(student_id, course_id)
            System.out.println("Error enrolling student: " + e.getMessage());
        }
    }

    public static void viewEnrollments() {
        String sql = """
            SELECT e.enrollment_id, s.name AS student_name, c.course_name, e.enrollment_date
            FROM enrollments e
            JOIN students s ON e.student_id = s.student_id
            JOIN courses c ON e.course_id = c.course_id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Enrollment List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("enrollment_id") + " | "
                        + rs.getString("student_name") + " | "
                        + rs.getString("course_name") + " | "
                        + rs.getDate("enrollment_date"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching enrollments: " + e.getMessage());
        }
    }
}