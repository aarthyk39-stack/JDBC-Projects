package hospitalmanagement;

import java.sql.*;
import java.util.Scanner;

public class AppointmentDAO {

    public static void bookAppointment(Scanner sc) {
        try (Connection conn = DBConnection.getConnection()) {

            Integer patientId = PatientDAO.findPatientId(sc, conn);
            if (patientId == null) return;

            Integer doctorId = DoctorDAO.findDoctorId(sc, conn);
            if (doctorId == null) return;

            String date;
            do {
                System.out.print("Enter appointment date (yyyy-MM-dd): ");
                date = sc.nextLine();
                if (!Validator.isValidDate(date)) System.out.println("Invalid date!");
            } while (!Validator.isValidDate(date));

            String time;
            do {
                System.out.print("Enter appointment time (HH:mm): ");
                time = sc.nextLine();
                if (!Validator.isValidTime(time)) System.out.println("Invalid time! Use HH:mm (e.g. 14:30)");
            } while (!Validator.isValidTime(time));

            String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);
                ps.setDate(3, Date.valueOf(date));
                ps.setTime(4, Time.valueOf(time + ":00"));
                int rows = ps.executeUpdate();
                System.out.println(rows + " appointment booked successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }

    public static void viewAppointments() {
        String sql = """
            SELECT a.appointment_id, p.name AS patient_name, d.name AS doctor_name,
                   d.specialization, a.appointment_date, a.appointment_time, a.status
            FROM appointments a
            JOIN patients p ON a.patient_id = p.patient_id
            JOIN doctors d ON a.doctor_id = d.doctor_id
            ORDER BY a.appointment_date, a.appointment_time
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n--- Appointment List ---");
            while (rs.next()) {
                System.out.println(rs.getInt("appointment_id") + " | " + rs.getString("patient_name") + " | "
                        + rs.getString("doctor_name") + " (" + rs.getString("specialization") + ") | "
                        + rs.getDate("appointment_date") + " " + rs.getTime("appointment_time") + " | "
                        + rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching appointments: " + e.getMessage());
        }
    }

    public static void updateStatus(Scanner sc) {
        String idInput;
        do {
            System.out.print("Enter appointment_id: ");
            idInput = sc.nextLine();
            if (!Validator.isPositiveInteger(idInput)) System.out.println("Invalid ID!");
        } while (!Validator.isPositiveInteger(idInput));
        int id = Integer.parseInt(idInput);

        String status;
        do {
            System.out.print("Enter status (Scheduled/Completed/Cancelled): ");
            status = sc.nextLine();
            if (!Validator.isValidStatus(status)) System.out.println("Invalid status!");
        } while (!Validator.isValidStatus(status));

        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Status updated!" : "No appointment found with that ID.");
        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
        }
    }

    public static Integer findAppointmentId(Scanner sc, Connection conn) throws SQLException {
        Integer patientId = PatientDAO.findPatientId(sc, conn);
        if (patientId == null) return null;

        String sql = """
            SELECT a.appointment_id, d.name AS doctor_name, a.appointment_date, a.appointment_time
            FROM appointments a JOIN doctors d ON a.doctor_id = d.doctor_id
            WHERE a.patient_id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            java.util.List<Integer> ids = new java.util.ArrayList<>();
            System.out.println("Appointments for this patient:");
            while (rs.next()) {
                ids.add(rs.getInt("appointment_id"));
                System.out.println(rs.getInt("appointment_id") + " | Dr. " + rs.getString("doctor_name")
                        + " | " + rs.getDate("appointment_date") + " " + rs.getTime("appointment_time"));
            }
            if (ids.isEmpty()) { System.out.println("No appointments found for this patient."); return null; }
            if (ids.size() == 1) return ids.get(0);

            int chosenId;
            do {
                System.out.print("Multiple appointments. Enter correct appointment_id: ");
                String idInput = sc.nextLine();
                chosenId = Validator.isPositiveInteger(idInput) ? Integer.parseInt(idInput) : -1;
                if (!ids.contains(chosenId)) System.out.println("Invalid selection!");
            } while (!ids.contains(chosenId));
            return chosenId;
        }
    }
}