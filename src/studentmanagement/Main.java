package studentmanagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Student Management =====");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Update Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Add Course");
            System.out.println("6. View Courses");
            System.out.println("7. Enroll Student");
            System.out.println("8. View Enrollments");
            System.out.println("9. Mark Attendance");
            System.out.println("10. View Attendance");
            System.out.println("11. View Attendance Percentage");
            System.out.println("12. Enter Marks");
            System.out.println("13. View Marks");
            System.out.println("14. Generate Result");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            String input = sc.nextLine();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                choice = -1;
            }

            switch (choice) {
                case 1 -> StudentDAO.addStudent(sc);
                case 2 -> StudentDAO.viewStudents();
                case 3 -> StudentDAO.updateStudent(sc);
                case 4 -> StudentDAO.deleteStudent(sc);
                case 5 -> CourseDAO.addCourse(sc);
                case 6 -> CourseDAO.viewCourses();
                case 7 -> EnrollmentDAO.enrollStudent(sc);
                case 8 -> EnrollmentDAO.viewEnrollments();
                case 9 -> AttendanceDAO.markAttendance(sc);
                case 10 -> AttendanceDAO.viewAttendance();
                case 11 -> AttendanceDAO.viewAttendancePercentage();
                case 12 -> MarksDAO.enterMarks(sc);
                case 13 -> MarksDAO.viewMarks();
                case 14 -> ResultDAO.generateResult(sc);
                case 0 -> System.out.println("Exiting...");
                case -1 -> {}
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 0);

        sc.close();
    }
}