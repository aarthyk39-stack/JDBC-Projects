package studentmanagement;

import java.sql.Connection;

public class ConnectionTest {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("Connection successful!");
            }
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}