package onlineexammanagement;

import java.sql.*;
import java.util.Scanner;

public class ExamDAO {

    public static void createExam(Scanner sc) {
        String name;
        do {
            System.out.print("Enter exam name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name)) System.out.println("Cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String subject;
        do {
            System.out.print("Enter subject: ");
            subject = sc.nextLine();
            if (!Validator.isNotEmpty(subject)) System.out.println("Cannot be empty!");
        } while (!Validator.isNotEmpty(subject));

        String durationInput;
        do {
            System.out.print("Enter duration (minutes): ");
            durationInput = sc.nextLine();
            if (!Validator.isPositiveInteger(durationInput)) System.out.println("Invalid duration!");
        } while (!Validator.isPositiveInteger(durationInput));

        String sql = "INSERT INTO exams (exam_name, subject, duration_minutes, total_marks) VALUES (?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, subject);
            ps.setInt(3, Integer.parseInt(durationInput));
            int rows = ps.executeUpdate();
            System.out.println(rows + " exam created! Total marks will update automatically as you add questions.");
        } catch (SQLException e) {
            System.out.println("Error creating exam: " + e.getMessage());
        }
    }

    public static void viewExams() {
        String sql = "SELECT * FROM exams";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n===== Available Exams =====");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %d min | %d marks%n",
                        rs.getInt("exam_id"), rs.getString("exam_name"), rs.getString("subject"),
                        rs.getInt("duration_minutes"), rs.getInt("total_marks"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching exams: " + e.getMessage());
        }
    }

    public static Integer findExamId(Scanner sc, Connection conn) throws SQLException {
        viewExams();
        String idInput;
        do {
            System.out.print("Enter exam_id: ");
            idInput = sc.nextLine();
            if (!Validator.isPositiveInteger(idInput)) System.out.println("Invalid ID!");
        } while (!Validator.isPositiveInteger(idInput));
        int examId = Integer.parseInt(idInput);

        String sql = "SELECT exam_id FROM exams WHERE exam_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return examId;
            System.out.println("No exam found with that ID.");
            return null;
        }
    }

    public static void recalculateTotalMarks(int examId, Connection conn) throws SQLException {
        String sql = "UPDATE exams SET total_marks = (SELECT COALESCE(SUM(marks),0) FROM questions WHERE exam_id = ?) WHERE exam_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, examId);
            ps.executeUpdate();
        }
    }
}