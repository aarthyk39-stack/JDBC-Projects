package onlineexammanagement;

import java.sql.*;
import java.util.Scanner;

public class QuestionDAO {

    public static void addQuestion(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer examId = ExamDAO.findExamId(sc, conn);
            if (examId == null) return;

            String qText;
            do {
                System.out.print("Enter question text: ");
                qText = sc.nextLine();
                if (!Validator.isNotEmpty(qText)) System.out.println("Cannot be empty!");
            } while (!Validator.isNotEmpty(qText));

            System.out.print("Enter option A: ");
            String a = sc.nextLine();
            System.out.print("Enter option B: ");
            String b = sc.nextLine();
            System.out.print("Enter option C: ");
            String c = sc.nextLine();
            System.out.print("Enter option D: ");
            String d = sc.nextLine();

            String correct;
            do {
                System.out.print("Enter correct option (A/B/C/D): ");
                correct = sc.nextLine().toUpperCase();
                if (!Validator.isValidOption(correct)) System.out.println("Must be A, B, C, or D!");
            } while (!Validator.isValidOption(correct));

            String marksInput;
            do {
                System.out.print("Enter marks for this question: ");
                marksInput = sc.nextLine();
                if (!Validator.isPositiveInteger(marksInput)) System.out.println("Invalid marks!");
            } while (!Validator.isPositiveInteger(marksInput));

            String sql = "INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_option, marks) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, examId);
                ps.setString(2, qText);
                ps.setString(3, a);
                ps.setString(4, b);
                ps.setString(5, c);
                ps.setString(6, d);
                ps.setString(7, correct);
                ps.setInt(8, Integer.parseInt(marksInput));
                ps.executeUpdate();
            }

            ExamDAO.recalculateTotalMarks(examId, conn);
            System.out.println("Question added! Exam total marks updated.");

        } catch (SQLException e) {
            System.out.println("Error adding question: " + e.getMessage());
        }
    }

    public static void viewQuestions(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {
            Integer examId = ExamDAO.findExamId(sc, conn);
            if (examId == null) return;

            String sql = "SELECT * FROM questions WHERE exam_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, examId);
                ResultSet rs = ps.executeQuery();
                System.out.println("\n--- Questions ---");
                while (rs.next()) {
                    System.out.printf("Q%d: %s [Marks: %d]%n", rs.getInt("question_id"),
                            rs.getString("question_text"), rs.getInt("marks"));
                    System.out.println("  A) " + rs.getString("option_a") + "  B) " + rs.getString("option_b"));
                    System.out.println("  C) " + rs.getString("option_c") + "  D) " + rs.getString("option_d"));
                    System.out.println("  Correct: " + rs.getString("correct_option"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching questions: " + e.getMessage());
        }
    }
}