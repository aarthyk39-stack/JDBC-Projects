package studentmanagement;

import java.sql.*;
import java.util.Scanner;

public class CourseDAO {

    public static void addCourse(Scanner sc) {
        String name;
        do {
            System.out.print("Enter course name: ");
            name = sc.nextLine();
            if (!Validator.isNotEmpty(name))
                System.out.println("Course name cannot be empty!");
        } while (!Validator.isNotEmpty(name));

        String durationInput;
        do {
            System.out.print("Enter duration (months): ");
            durationInput = sc.nextLine();
            if (!Validator.isPositiveInteger(durationInput))
                System.out.println("Invalid duration! Must be a positive number.");
        } while (!Validator.isPositiveInteger(durationInput));
        int duration = Integer.parseInt(durationInput);

        String sql = "INSERT INTO courses (course_name, duration_months) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, duration);

            int rows = ps.executeUpdate();
            System.out.println(rows + " course added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    public static void viewCourses() {
        String sql = "SELECT * FROM courses";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- Course List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("course_id") + " | "
                        + rs.getString("course_name") + " | "
                        + rs.getInt("duration_months") + " months");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
    }
}