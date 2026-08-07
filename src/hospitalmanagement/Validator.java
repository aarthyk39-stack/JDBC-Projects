package hospitalmanagement;

import java.time.LocalDate;
import java.time.LocalTime;

public class Validator {

    public static boolean isValidName(String name) {
        return name != null && name.matches("^[a-zA-Z ]+$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    public static boolean isPositiveInteger(String input) {
        try {
            return Integer.parseInt(input.trim()) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidGender(String gender) {
        return gender != null && (gender.equalsIgnoreCase("Male")
                || gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("Other"));
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidTime(String time) {
        try {
            LocalTime.parse(time.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidStatus(String status) {
        return status != null && (status.equalsIgnoreCase("Scheduled")
                || status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("Cancelled"));
    }
}