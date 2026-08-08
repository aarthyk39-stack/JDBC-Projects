package onlineexammanagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;
        Integer loggedInStudentId = null;

        do {
            System.out.println("\n===== Online Examination System =====");
            System.out.println("1. Register (Student)");
            System.out.println("2. Login (Student)");
            System.out.println("3. Create Exam (Admin)");
            System.out.println("4. View Exams");
            System.out.println("5. Add Question (Admin)");
            System.out.println("6. View Questions (Admin)");
            System.out.println("7. Take Exam (Student - login required)");
            System.out.println("8. My Results (Student - login required)");
            System.out.println("9. Exam Statistics (Admin)");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            String input = sc.nextLine();
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Enter a number.");
                choice = -1;
            }

            switch (choice) {
                case 1 -> StudentDAO.registerStudent(sc);
                case 2 -> loggedInStudentId = StudentDAO.loginStudent(sc);
                case 3 -> ExamDAO.createExam(sc);
                case 4 -> ExamDAO.viewExams();
                case 5 -> QuestionDAO.addQuestion(sc);
                case 6 -> QuestionDAO.viewQuestions(sc);
                case 7 -> {
                    if (loggedInStudentId == null) System.out.println("Please login first!");
                    else ExamAttemptDAO.takeExam(sc, loggedInStudentId);
                }
                case 8 -> {
                    if (loggedInStudentId == null) System.out.println("Please login first!");
                    else ResultDAO.viewMyResults(loggedInStudentId);
                }
                case 9 -> ResultDAO.viewExamStatistics(sc);
                case 0 -> System.out.println("Exiting...");
                case -1 -> {}
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 0);

        sc.close();
    }
}
