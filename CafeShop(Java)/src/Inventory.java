import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Inventory {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/coffeestore?useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "EzEn003^IT^LOL$";

    public static void displayMenu(Scanner scanner) {
        try {
            while (true) {

                System.out.println("\n╭───────────────────────────────────╮");
                System.out.println("│        Coffee Menu Options        │");
                System.out.println("├───────────────────────────────────┤");
                System.out.println("│         1. Add Coffee             │");
                System.out.println("│         2. Update Coffee          │");
                System.out.println("│         3. Delete Coffee          │");
                System.out.println("│         4. Display Coffees        │");
                System.out.println("│         5. Back to Main Menu      │");
                System.out.println("╰───────────────────────────────────╯");
                System.out.print("Enter your choice (1-5): ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        addCoffee(scanner);
                        break;
                    case 2:
                        updateCoffee(scanner);
                        break;
                    case 3:
                        deleteCoffee(scanner);
                        break;
                    case 4:
                        displayCoffees();
                        break;
                    case 5:
                        System.out.println("Returning to Main Menu.");
                        Main.AccountMainSystem();
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addCoffee(Scanner scanner) throws SQLException {
        System.out.println("Enter coffee details:");

        System.out.print("Coffee Type: ");
        String coffeeType = scanner.nextLine();

        System.out.print("Size: ");
        String size = scanner.nextLine();

        System.out.print("Sugar Level: ");
        int sugarLevel = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Condition: ");
        String condition = scanner.nextLine();

        System.out.print("Coffee Id: ");
        String coffeeId = scanner.nextLine();

        System.out.print("Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        // Create a Coffee object
        Coffee coffee = new Coffee(coffeeId, coffeeType, size, sugarLevel, condition, price, stock);

        // Insert the coffee into the database
        insertCoffeeIntoDatabase(coffee);

        System.out.println("Coffee added successfully! Coffee ID: " + coffee.getCoffeeID());
    }

    private static void updateCoffee(Scanner scanner) throws SQLException {
        System.out.print("Enter the Coffee ID to update: ");
        String coffeeId = scanner.nextLine();

        // Check if the coffee with the given ID exists
        Coffee existingCoffee = getCoffeeById(coffeeId);
        if (existingCoffee == null) {
            System.out.println("Coffee not found with ID: " + coffeeId);
            return;
        }

        System.out.println("Current coffee details:");
        displayCoffeeDetails(existingCoffee);

        System.out.println("\nEnter new coffee details:");

        System.out.print("Coffee Type: ");
        String coffeeType = scanner.nextLine();

        System.out.print("Size: ");
        String size = scanner.nextLine();

        System.out.print("Sugar Level: ");
        int sugarLevel = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Condition: ");
        String condition = scanner.nextLine();

        System.out.print("Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume the newline character

        System.out.print("Stock: ");
        int stock = scanner.nextInt();
        scanner.nextLine();

        // Update the coffee in the database
        updateCoffeeInDatabase(coffeeId, coffeeType, size, sugarLevel, condition, price, stock);

        System.out.println("Coffee updated successfully! Updated coffee details:");
        displayCoffeeDetails(getCoffeeById(coffeeId));
    }

    private static void deleteCoffee(Scanner scanner) throws SQLException {
        System.out.print("Enter the Coffee ID to delete: ");
        String coffeeId = scanner.nextLine();

        // Check if the coffee with the given ID exists
        Coffee existingCoffee = getCoffeeById(coffeeId);
        if (existingCoffee == null) {
            System.out.println("Coffee not found with ID: " + coffeeId);
            return;
        }

        // Delete the coffee from the database
        deleteCoffeeFromDatabase(coffeeId);

        System.out.println("Coffee deleted successfully!");
    }

    private static void displayCoffees() throws SQLException {
        System.out.println("All Coffees:");
        for (Coffee coffee : getAllCoffees()) {
            displayCoffeeDetails(coffee);
        }
    }

    private static boolean headerDisplayed = false;

    private static void displayCoffeeHeader() {
        System.out.println("╭───────────────────────┬───────────────────────┬───────────────┬───────────────┬─────────────────┬───────────────┬──────────╮");
        System.out.println("│   Coffee ID           │   Coffee Type         │   Size        │   Sugar Level │   Condition     │   Price       │   Stock  │");
        System.out.println("├───────────────────────┼───────────────────────┼───────────────┼───────────────┼─────────────────┼───────────────┼──────────┤");
    }

    // Method to display a coffee
    private static void displayCoffee(Coffee coffee) {
        System.out.printf("│   %-20s│   %-20s│   %-11s │    %-11s│  %-15s│ $%-13s│  %-8s│%n", 
                        coffee.getCoffeeID(), coffee.getType(), coffee.getSize(), 
                        coffee.getSugarLevel(), coffee.getCondition(), coffee.getPrice(), coffee.getStock());
        System.out.println("╰───────────────────────┴───────────────────────┴───────────────┴───────────────┴─────────────────┴───────────────┴──────────╯");
    }

    private static void displayCoffeeDetails(Coffee coffee) {

        // Display header only if it has not been displayed before
        if (!headerDisplayed) {
            displayCoffeeHeader();
            headerDisplayed = true;
        }
        displayCoffee(coffee);
    }

    public static void insertCoffeeIntoDatabase(Coffee coffee) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO inventory_table (CoffeeID, type, size, sugar_level, `condition`, price, stock) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, coffee.getCoffeeID());
                preparedStatement.setString(2, coffee.getType());
                preparedStatement.setString(3, coffee.getSize());
                preparedStatement.setInt(4, coffee.getSugarLevel());
                preparedStatement.setString(5, coffee.getCondition());
                preparedStatement.setDouble(6, coffee.getPrice());
                preparedStatement.setInt(7, coffee.getStock());

                preparedStatement.executeUpdate();
            }
        }
    }

    public static void updateCoffeeInDatabase(String coffeeId, String coffeeType, String size, int sugarLevel, String condition, double price, int stock) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE inventory_table SET type = ?, size = ?, sugar_level = ?, `condition` = ?, price = ?, stock = ? WHERE CoffeeID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, coffeeType);
                preparedStatement.setString(2, size);
                preparedStatement.setInt(3, sugarLevel);
                preparedStatement.setString(4, condition);
                preparedStatement.setDouble(5, price);
                preparedStatement.setInt(6, stock);
                preparedStatement.setString(7, coffeeId);

                preparedStatement.executeUpdate();
            }
        }
    }

    public static void deleteCoffeeFromDatabase(String coffeeId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM inventory_table WHERE CoffeeID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, coffeeId);
                preparedStatement.executeUpdate();
            }
        }
    }

    public static List<Coffee> getAllCoffees() throws SQLException {
        List<Coffee> coffees = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM inventory_table";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Coffee coffee = new Coffee(
                                resultSet.getString("CoffeeID"),
                                resultSet.getString("type"),
                                resultSet.getString("size"),
                                resultSet.getInt("sugar_level"),
                                resultSet.getString("condition"),
                                resultSet.getDouble("price"),
                                resultSet.getInt("stock")
                        );
                        coffees.add(coffee);
                    }
                }
            }
        }

        return coffees;
    }

    public static Coffee getCoffeeById(String coffeeId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM inventory_table WHERE CoffeeID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, coffeeId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Coffee(
                                resultSet.getString("CoffeeID"),
                                resultSet.getString("type"),
                                resultSet.getString("size"),
                                resultSet.getInt("sugar_level"),
                                resultSet.getString("condition"),
                                resultSet.getDouble("price"),
                                resultSet.getInt("stock")
                        );
                    }
                }
            }
        }
        return null;
    }
}
