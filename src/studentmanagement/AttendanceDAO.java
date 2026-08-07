package studentmanagement;

import java.sql.*;
import java.util.Scanner;

public class AttendanceDAO {

    public static void markAttendance(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer enrollmentId = EnrollmentDAO.findEnrollmentId(sc, conn);
            if (enrollmentId == null) return;

            String date;
            do {
                System.out.print("Enter attendance date (yyyy-MM-dd): ");
                date = sc.nextLine();
                if (!Validator.isValidDate(date))
                    System.out.println("Invalid date format! Use yyyy-MM-dd.");
            } while (!Validator.isValidDate(date));

            String status;
            do {
                System.out.print("Enter status (Present/Absent): ");
                status = sc.nextLine();
                if (!Validator.isValidStatus(status))
                    System.out.println("Invalid status! Enter Present or Absent.");
            } while (!Validator.isValidStatus(status));

            String sql = "INSERT INTO attendance (enrollment_id, attendance_date, status) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, enrollmentId);
                ps.setDate(2, Date.valueOf(date));
                ps.setString(3, status);

                int rows = ps.executeUpdate();
                System.out.println(rows + " attendance marked successfully!");
            }

        } catch (SQLException e) {
            // duplicate attendance for same date will fail due to UNIQUE(enrollment_id, attendance_date)
            System.out.println("Error marking attendance: " + e.getMessage());
        }
    }

    public static void viewAttendance() {
        String sql = """
            SELECT a.attendance_id, s.name AS student_name, c.course_name,
                   a.attendance_date, a.status
            FROM attendance a
            JOIN enrollments e ON a.enrollment_id = e.enrollment_id
            JOIN students s ON e.student_id = s.student_id
            JOIN courses c ON e.course_id = c.course_id
            ORDER BY a.attendance_date
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Attendance List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("attendance_id") + " | "
                        + rs.getString("student_name") + " | "
                        + rs.getString("course_name") + " | "
                        + rs.getDate("attendance_date") + " | "
                        + rs.getString("status"));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching attendance: " + e.getMessage());
        }
    }

    public static void viewAttendancePercentage() {
        String sql = """
            SELECT s.name AS student_name, c.course_name,
                   COUNT(*) AS total_classes,
                   SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS present_count
            FROM attendance a
            JOIN enrollments e ON a.enrollment_id = e.enrollment_id
            JOIN students s ON e.student_id = s.student_id
            JOIN courses c ON e.course_id = c.course_id
            GROUP BY e.enrollment_id, s.name, c.course_name
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Attendance Percentage ---");
            while (rs.next()) {
                int total = rs.getInt("total_classes");
                int present = rs.getInt("present_count");
                double percentage = (present * 100.0) / total;

                System.out.printf("%s | %s | %d/%d classes | %.2f%%%n",
                        rs.getString("student_name"), rs.getString("course_name"),
                        present, total, percentage);
            }

        } catch (SQLException e) {
            System.out.println("Error calculating percentage: " + e.getMessage());
        }
    }
}