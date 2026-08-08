package ecommercemanagement;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== E-Commerce Store =====");
            System.out.println("1. Add Product (Admin)");
            System.out.println("2. View Product Catalog");
            System.out.println("3. Register Customer");
            System.out.println("4. View Customers (Admin)");
            System.out.println("5. Add to Cart");
            System.out.println("6. View Cart");
            System.out.println("7. Place Order");
            System.out.println("8. Track My Orders");
            System.out.println("9. View Order Details");
            System.out.println("10. Update Order Status (Admin)");
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
                case 1 -> ProductDAO.addProduct(sc);
                case 2 -> ProductDAO.viewCatalog();
                case 3 -> CustomerDAO.addCustomer(sc);
                case 4 -> CustomerDAO.viewCustomers();
                case 5 -> CartDAO.addToCart(sc);
                case 6 -> CartDAO.viewCart();
                case 7 -> OrderDAO.placeOrder(sc);
                case 8 -> OrderDAO.trackOrders(sc);
                case 9 -> OrderDAO.viewOrderDetails(sc);
                case 10 -> OrderDAO.updateOrderStatus(sc);
                case 0 -> System.out.println("Exiting...");
                case -1 -> {}
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 0);

        sc.close();
    }
}