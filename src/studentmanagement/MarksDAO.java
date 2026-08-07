package studentmanagement;

import java.sql.*;
import java.util.Scanner;

public class MarksDAO {

    public static void enterMarks(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer enrollmentId = EnrollmentDAO.findEnrollmentId(sc, conn);
            if (enrollmentId == null) return;

            String examType;
            do {
                System.out.print("Enter exam type (e.g. Internal Test 1, Final Exam): ");
                examType = sc.nextLine();
                if (!Validator.isNotEmpty(examType))
                    System.out.println("Exam type cannot be empty!");
            } while (!Validator.isNotEmpty(examType));

            String totalStr, scoredStr;
            do {
                System.out.print("Enter total marks: ");
                totalStr = sc.nextLine();
                System.out.print("Enter marks scored: ");
                scoredStr = sc.nextLine();
                if (!Validator.isValidMarks(totalStr, scoredStr))
                    System.out.println("Invalid marks! Scored marks can't exceed total, and both must be positive numbers.");
            } while (!Validator.isValidMarks(totalStr, scoredStr));

            int total = Integer.parseInt(totalStr);
            int scored = Integer.parseInt(scoredStr);

            String sql = "INSERT INTO marks (enrollment_id, exam_type, total_marks, marks_scored) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, enrollmentId);
                ps.setString(2, examType);
                ps.setInt(3, total);
                ps.setInt(4, scored);

                int rows = ps.executeUpdate();
                System.out.println(rows + " marks entered successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Error entering marks: " + e.getMessage());
        }
    }

    public static void viewMarks() {
        String sql = """
            SELECT m.mark_id, s.name AS student_name, c.course_name,
                   m.exam_type, m.total_marks, m.marks_scored
            FROM marks m
            JOIN enrollments e ON m.enrollment_id = e.enrollment_id
            JOIN students s ON e.student_id = s.student_id
            JOIN courses c ON e.course_id = c.course_id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Marks List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("mark_id") + " | "
                        + rs.getString("student_name") + " | "
                        + rs.getString("course_name") + " | "
                        + rs.getString("exam_type") + " | "
                        + rs.getInt("marks_scored") + "/" + rs.getInt("total_marks"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching marks: " + e.getMessage());
        }
    }
}