package onlineexammanagement;

import java.sql.*;
import java.util.Scanner;

public class ExamAttemptDAO {

    public static void takeExam(Scanner sc, int studentId) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer examId = ExamDAO.findExamId(sc, conn);
            if (examId == null) return;

            String questionSql = "SELECT * FROM questions WHERE exam_id = ?";
            int score = 0;
            int totalMarks = 0;
            int questionCount = 0;

            try (PreparedStatement ps = conn.prepareStatement(questionSql)) {
                ps.setInt(1, examId);
                ResultSet rs = ps.executeQuery();

                System.out.println("\n===== Starting Exam =====");
                while (rs.next()) {
                    questionCount++;
                    int marks = rs.getInt("marks");
                    totalMarks += marks;
                    String correctOption = rs.getString("correct_option");

                    System.out.println("\nQ" + questionCount + ": " + rs.getString("question_text"));
                    System.out.println("A) " + rs.getString("option_a"));
                    System.out.println("B) " + rs.getString("option_b"));
                    System.out.println("C) " + rs.getString("option_c"));
                    System.out.println("D) " + rs.getString("option_d"));

                    String answer;
                    do {
                        System.out.print("Your answer (A/B/C/D): ");
                        answer = sc.nextLine().toUpperCase();
                        if (!Validator.isValidOption(answer)) System.out.println("Enter A, B, C, or D!");
                    } while (!Validator.isValidOption(answer));

                    if (answer.equals(correctOption)) {
                        score += marks;
                        System.out.println("Correct!");
                    } else {
                        System.out.println("Incorrect. Correct answer was: " + correctOption);
                    }
                }
            }

            if (questionCount == 0) {
                System.out.println("This exam has no questions yet.");
                return;
            }

            String resultSql = "INSERT INTO results (student_id, exam_id, score_obtained, total_marks, exam_date) VALUES (?, ?, ?, ?, CURDATE())";
            try (PreparedStatement ps = conn.prepareStatement(resultSql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, examId);
                ps.setInt(3, score);
                ps.setInt(4, totalMarks);
                ps.executeUpdate();
            }

            double percentage = (score * 100.0) / totalMarks;
            System.out.println("\n===== Exam Completed! =====");
            System.out.printf("Score: %d / %d (%.2f%%)%n", score, totalMarks, percentage);
            System.out.println(percentage >= 40 ? "Result: PASS" : "Result: FAIL");

        } catch (SQLException e) {
            System.out.println("Error during exam: " + e.getMessage());
        }
    }
}
