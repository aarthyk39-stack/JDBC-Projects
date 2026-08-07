package studentmanagement;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z ]+$");
    }

       public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && Pattern.matches(emailRegex, email.trim());
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    public static boolean isPositiveInteger(String input) {
        try {
            int value = Integer.parseInt(input.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidMarks(String totalStr, String scoredStr) {
        if (!isPositiveInteger(totalStr) || !isPositiveInteger(scoredStr)) return false;
        int total = Integer.parseInt(totalStr.trim());
        int scored = Integer.parseInt(scoredStr.trim());
        return scored <= total;
    }

    public static boolean isValidStatus(String status) {
        return status != null &&
                (status.equalsIgnoreCase("Present") || status.equalsIgnoreCase("Absent"));
    }
}