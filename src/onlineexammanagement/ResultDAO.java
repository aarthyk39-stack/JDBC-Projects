package onlineexammanagement;

import java.sql.*;
import java.util.Scanner;

public class ResultDAO {

    public static void viewMyResults(int studentId) {
        String sql = """
            SELECT e.exam_name, e.subject, r.score_obtained, r.total_marks, r.exam_date
            FROM results r
            JOIN exams e ON r.exam_id = e.exam_id
            WHERE r.student_id = ?
            ORDER BY r.exam_date DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== My Results =====");
            boolean found = false;
            while (rs.next()) {
                found = true;
                double pct = (rs.getInt("score_obtained") * 100.0) / rs.getInt("total_marks");
                System.out.printf("%s (%s) | %d/%d (%.2f%%) | %s%n",
                        rs.getString("exam_name"), rs.getString("subject"),
                        rs.getInt("score_obtained"), rs.getInt("total_marks"), pct, rs.getDate("exam_date"));
            }
            if (!found) System.out.println("You haven't attempted any exams yet.");
        } catch (SQLException e) {
            System.out.println("Error fetching results: " + e.getMessage());
        }
    }

    public static void viewExamStatistics(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {
            Integer examId = ExamDAO.findExamId(sc, conn);
            if (examId == null) return;

            String sql = """
                SELECT e.exam_name,
                       COUNT(r.result_id) AS attempts,
                       AVG(r.score_obtained) AS avg_score,
                       MAX(r.score_obtained) AS highest_score,
                       MIN(r.score_obtained) AS lowest_score
                FROM exams e
                LEFT JOIN results r ON e.exam_id = r.exam_id
                WHERE e.exam_id = ?
                GROUP BY e.exam_id, e.exam_name
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, examId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int attempts = rs.getInt("attempts");
                    System.out.println("\n===== Exam Statistics: " + rs.getString("exam_name") + " =====");
                    System.out.println("Total attempts: " + attempts);
                    if (attempts > 0) {
                        System.out.printf("Average score: %.2f%n", rs.getDouble("avg_score"));
                        System.out.println("Highest score: " + rs.getInt("highest_score"));
                        System.out.println("Lowest score : " + rs.getInt("lowest_score"));
                    } else {
                        System.out.println("No attempts yet.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching statistics: " + e.getMessage());
        }
    }
}