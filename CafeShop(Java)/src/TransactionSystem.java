import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class TransactionSystem {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/coffeestore?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "EzEn003^IT^LOL$";
    private static final String RECORDER_TABLE_NAME = "record_table";
    
    public static void displayMenu(Scanner scanner) {
        try {
            while (true) {

                System.out.println("\n╭───────────────────────────────────────╮");
                System.out.println("│      Transaction System Options       │");
                System.out.println("├───────────────────────────────────────┤");
                System.out.println("│ 1. Display All Transactions           │");
                System.out.println("│ 2. Search Transaction                 │");
                System.out.println("│ 3. Display Total Record               │");
                System.out.println("│ 4. Back to Main Menu                  │");
                System.out.println("╰───────────────────────────────────────╯");
                System.out.print("Enter your choice (1-4): ");


                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        displayAllTransactions();
                        break;
                    case 2:
                        searchTransaction(scanner);
                        break;
                    case 3:
                        updateRecorderTable();
                        displayRecorderTable();
                        break;
                    case 4:
                        System.out.println("Returning to Main Menu.");
                        Main.AccountMainSystem();;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 3.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void displayAllTransactions() throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM transaction_table";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("\nAll Transactions:");
                    while (resultSet.next()) {
                        displayTransactionDetails(resultSet);
                    }
                }
            }
        }
    }

    private static void searchTransaction(Scanner scanner) throws SQLException {

        System.out.println("\n╭───────────────────────────────────────╮");
        System.out.println("│          Search Transaction           │");
        System.out.println("├───────────────────────────────────────┤");
        System.out.println("│ 1. Search by Transaction ID           │");
        System.out.println("│ 2. Search by Date                     │");
        System.out.println("╰───────────────────────────────────────╯");
        System.out.print("Enter your choice (1 or 2): ");

        

        int choice = scanner.nextInt();
        scanner.nextLine(); 

        switch (choice) {
            case 1:
                System.out.print("Enter Transaction ID: ");
                int transactionId = scanner.nextInt();
                searchTransactionById(transactionId);
                break;
            case 2:
                System.out.print("Enter Date (YYYY-MM-DD): ");
                String dateString = scanner.nextLine();
                LocalDate date = LocalDate.parse(dateString);
                searchTransactionByDate(date);
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private static void searchTransactionById(int transactionId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM transaction_table WHERE transactionID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, transactionId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("\nTransaction Details for ID " + transactionId + ":");
                        displayTransactionDetails(resultSet);
                    } else {
                        System.out.println("Transaction not found with ID: " + transactionId);
                    }
                }
            }
        }
    }

    private static void searchTransactionByDate(LocalDate date) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM transaction_table WHERE date = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDate(1, Date.valueOf(date));

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("\nTransaction Details for Date " + date + ":");
                        displayTransactionDetails(resultSet);
                    } else {
                        System.out.println("No transactions found for date: " + date);
                    }
                }
            }
        }
    }

    private static boolean detailsHeaderDisplayed = false;

    private static void displayDetailsHeader() {
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("| %-15s | %-12s | %-17s | %-21s | %-15s  |%n",
                "Transaction ID", "Date", "Customer per Day", "Quantity Sale per Day", "Revenue per Day");
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private static void displayTransactionDetails(ResultSet resultSet) throws SQLException {

        if (!detailsHeaderDisplayed) {
            displayDetailsHeader();
            detailsHeaderDisplayed = true;
        }
        System.out.printf("| %-15s | %-12s | %-17s | %-21s | $%-15s |%n",
                resultSet.getInt("transactionID"), resultSet.getDate("date"),
                resultSet.getInt("customer_perDay"), resultSet.getInt("quantity_sale_perDay"),
                resultSet.getDouble("revenue_perDay"));
        System.out.println("-------------------------------------------------------------------------------------------------");
    }


    private static void updateRecorderTable() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            // Calculate the sums
            String sumQuery = "SELECT SUM(customer_perDay) AS total_customer, " +
                              "SUM(quantity_sale_perDay) AS total_quantity_sale, " +
                              "SUM(revenue_perDay) AS total_revenue FROM transaction_table";
    
            try (PreparedStatement sumStatement = connection.prepareStatement(sumQuery)) {
                try (ResultSet sumResult = sumStatement.executeQuery()) {
                    if (sumResult.next()) {
                        int totalCustomer = sumResult.getInt("total_customer");
                        int totalQuantitySale = sumResult.getInt("total_quantity_sale");
                        double totalRevenue = sumResult.getDouble("total_revenue");
    
                        // Update the values in recorder_table
                        String updateQuery = "UPDATE " + RECORDER_TABLE_NAME +
                                             " SET total_customer = ?, " +
                                             " total_quantity_sale = ?, " +
                                             " total_revenue = ?";
    
                        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                            updateStatement.setInt(1, totalCustomer);
                            updateStatement.setInt(2, totalQuantitySale);
                            updateStatement.setDouble(3, totalRevenue);
    
                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                System.out.println("Record table updated successfully.");
                            } else {
                                System.out.println("Failed to update record table. No rows affected.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for debugging
            System.out.println("Failed to update record table. Error: " + e.getMessage());
        }
    }
    
    public static void displayRecorderTable() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM " + RECORDER_TABLE_NAME;
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("\nRecord Table:");
                    while (resultSet.next()) {
                        displayRecorderTableDetails(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print the stack trace for debugging
            System.out.println("Failed to display record table. Error: " + e.getMessage());
        }
    }
    

    private static void displayRecorderTableDetails(ResultSet resultSet) throws SQLException {
        System.out.println("-------------------------");
        System.out.println("Total Customer: " + resultSet.getInt("total_customer"));
        System.out.println("Total Quantity Sale: " + resultSet.getInt("total_quantity_sale"));
        System.out.println("Total Revenue: $" + resultSet.getDouble("total_revenue"));
        System.out.println("-------------------------");
    }

}



    
    