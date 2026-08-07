package studentmanagement;

import java.sql.*;
import java.util.Scanner;

public class ResultDAO {

    private static final double PASS_PERCENTAGE = 40.0;

    public static void generateResult(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer enrollmentId = EnrollmentDAO.findEnrollmentId(sc, conn);
            if (enrollmentId == null) return;

            String infoSql = """
                SELECT s.name AS student_name, c.course_name
                FROM enrollments e
                JOIN students s ON e.student_id = s.student_id
                JOIN courses c ON e.course_id = c.course_id
                WHERE e.enrollment_id = ?
                """;

            String studentName = null, courseName = null;
            try (PreparedStatement ps = conn.prepareStatement(infoSql)) {
                ps.setInt(1, enrollmentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    studentName = rs.getString("student_name");
                    courseName = rs.getString("course_name");
                }
            }

            String marksSql = "SELECT exam_type, total_marks, marks_scored FROM marks WHERE enrollment_id = ?";

            int totalMarksSum = 0, scoredMarksSum = 0;

            System.out.println("\n===== Result =====");
            System.out.println("Student: " + studentName);
            System.out.println("Course : " + courseName);
            System.out.println("-------------------");

            try (PreparedStatement ps = conn.prepareStatement(marksSql)) {
                ps.setInt(1, enrollmentId);
                ResultSet rs = ps.executeQuery();

                boolean hasMarks = false;
                while (rs.next()) {
                    hasMarks = true;
                    int total = rs.getInt("total_marks");
                    int scored = rs.getInt("marks_scored");
                    totalMarksSum += total;
                    scoredMarksSum += scored;

                    System.out.printf("%-20s : %d / %d%n", rs.getString("exam_type"), scored, total);
                }

                if (!hasMarks) {
                    System.out.println("No marks entered yet for this enrollment.");
                    return;
                }
            }

            double overallPercentage = (scoredMarksSum * 100.0) / totalMarksSum;
            String resultStatus = overallPercentage >= PASS_PERCENTAGE ? "PASS" : "FAIL";

            System.out.println("-------------------");
            System.out.printf("Total          : %d / %d%n", scoredMarksSum, totalMarksSum);
            System.out.printf("Overall Percent: %.2f%%%n", overallPercentage);
            System.out.println("Result         : " + resultStatus);

        } catch (SQLException e) {
            System.out.println("Error generating result: " + e.getMessage());
        }
    }
}