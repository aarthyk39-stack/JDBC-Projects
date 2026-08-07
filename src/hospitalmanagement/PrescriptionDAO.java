package hospitalmanagement;

import java.sql.*;
import java.util.Scanner;

public class PrescriptionDAO {

    public static void addPrescription(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer appointmentId = AppointmentDAO.findAppointmentId(sc, conn);
            if (appointmentId == null) return;

            String medicine;
            do {
                System.out.print("Enter medicine name: ");
                medicine = sc.nextLine();
                if (!Validator.isNotEmpty(medicine)) System.out.println("Cannot be empty!");
            } while (!Validator.isNotEmpty(medicine));

            System.out.print("Enter dosage (e.g. 500mg twice a day): ");
            String dosage = sc.nextLine();

            String durationInput;
            do {
                System.out.print("Enter duration (days): ");
                durationInput = sc.nextLine();
                if (!Validator.isPositiveInteger(durationInput)) System.out.println("Invalid duration!");
            } while (!Validator.isPositiveInteger(durationInput));
            int duration = Integer.parseInt(durationInput);

            String sql = "INSERT INTO prescriptions (appointment_id, medicine_name, dosage, duration_days) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, appointmentId);
                ps.setString(2, medicine);
                ps.setString(3, dosage);
                ps.setInt(4, duration);
                int rows = ps.executeUpdate();
                System.out.println(rows + " prescription added successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error adding prescription: " + e.getMessage());
        }
    }

    public static void viewPrescriptions() {
        String sql = """
            SELECT pr.prescription_id, p.name AS patient_name, d.name AS doctor_name,
                   pr.medicine_name, pr.dosage, pr.duration_days
            FROM prescriptions pr
            JOIN appointments a ON pr.appointment_id = a.appointment_id
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN doctors d ON a.doctor_id = d.doctor_id
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n--- Prescription List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("prescription_id") + " | " + rs.getString("patient_name") + " | Dr. "
                        + rs.getString("doctor_name") + " | " + rs.getString("medicine_name") + " | "
                        + rs.getString("dosage") + " | " + rs.getInt("duration_days") + " days");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching prescriptions: " + e.getMessage());
        }
    }
}