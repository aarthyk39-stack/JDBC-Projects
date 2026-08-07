package hospitalmanagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Hospital Management =====");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Add Doctor");
            System.out.println("4. View Doctors");
            System.out.println("5. Book Appointment");
            System.out.println("6. View Appointments");
            System.out.println("7. Update Appointment Status");
            System.out.println("8. Add Prescription");
            System.out.println("9. View Prescriptions");
            System.out.println("10. Delete Patient");
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
                case 1 -> PatientDAO.addPatient(sc);
                case 2 -> PatientDAO.viewPatients();
                case 3 -> DoctorDAO.addDoctor(sc);
                case 4 -> DoctorDAO.viewDoctors();
                case 5 -> AppointmentDAO.bookAppointment(sc);
                case 6 -> AppointmentDAO.viewAppointments();
                case 7 -> AppointmentDAO.updateStatus(sc);
                case 8 -> PrescriptionDAO.addPrescription(sc);
                case 9 -> PrescriptionDAO.viewPrescriptions();
                case 10 -> PatientDAO.deletePatient(sc);
                case 0 -> System.out.println("Exiting...");
                case -1 -> {}
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 0);

        sc.close();
    }
}