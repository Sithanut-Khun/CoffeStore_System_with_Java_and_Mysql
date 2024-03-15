import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderMenu {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/coffeestore?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "EzEn003^IT^LOL$";
    public static void displayMenu(Scanner scanner) {
        try {
            // boolean exit = false;
            while (true) {
                System.out.println("\n╔═════════════════════════════╗");
                System.out.println("║   Coffee Shop Order Menu    ║");
                System.out.println("╠═════════════════════════════╣");
                System.out.println("║ 1. Add Coffee to Order      ║");
                System.out.println("║ 2. Update Order             ║");
                System.out.println("║ 3. Cancel Order             ║");
                System.out.println("║ 4. Place Order              ║");
                System.out.println("║ 5. View All Order           ║");
                System.out.println("║ 6. Back to Main Menu        ║");
                System.out.println("╚═════════════════════════════╝");
                System.out.print("Enter your choice (1-6): ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                switch (choice) {
                    case 1:
                        addOrder(scanner);
                        break;
                    case 2:
                        updateOrder(scanner);
                        break;
                    case 3:
                        cancelOrder(scanner);
                        break;
                    case 4:
                        placeOrder(scanner);
                        break;
                    case 5:
                        displayOrders();
                        break;
                    case 6:
                        System.out.println("Returning to Main Menu.");
                        Main.AccountMainSystem();
                        break; // Set exit to true to break out of the loop
                        // return; // Back to the main menu
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addOrder(Scanner scanner) throws SQLException {
        System.out.println("Enter order details:");

        System.out.print("Coffee Type: ");
        String coffeeType = scanner.nextLine();

        System.out.print("Condition: ");
        String condition = scanner.nextLine();

        System.out.print("Size: ");
        String size = scanner.nextLine();

        System.out.print("Sugar Level: ");
        int sugarLevel = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        
        String coffeeid = getCoffeeIdFromInventory(coffeeType, condition, size, sugarLevel);

        if (coffeeid == null) {
            System.out.println("No matching coffee found in the inventory. Order cannot be placed.");
            return;
        }

        System.out.print("Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

    
        double totalPrice = calculateTotalPrice(coffeeid, quantity);

        // Create an Order object
        Order order = new Order(coffeeType, condition,size, sugarLevel, coffeeid, quantity);
        order.setTotalPrice(totalPrice);

        // Insert the order into the database
        insertOrderIntoDatabase(order);

        // Update stock in inventory_table
        updateInventoryStock(coffeeid, quantity, false);

        System.out.println("Order added successfully!");
    }

    private static void updateOrder(Scanner scanner) throws SQLException {
        System.out.print("Enter the Order ID to update: ");
        int orderId = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        // Check if the order with the given ID exists
        Order existingOrder = getOrderById(orderId);
        if (existingOrder == null) {
            System.out.println("Order not found with ID: " + orderId);
            return;
        }

        System.out.println("Current order details:");
        displayOrderDetails(existingOrder);

        String existingCoffeeId = existingOrder.getCoffeeID();
        int existingQuantity = existingOrder.getQuantity();


        updateInventoryStock(existingCoffeeId, existingQuantity, true); // Increase stock


        System.out.println("\nEnter new order details:");

        System.out.print("Coffee Type: ");
        String coffeeType = scanner.nextLine();

        System.out.print("Condition: ");
        String condition = scanner.nextLine();

        System.out.print("Size: ");
        String size = scanner.nextLine();

        System.out.print("Sugar Level: ");
        int sugarLevel = scanner.nextInt();
        scanner.nextLine(); 

        String coffeeid = getCoffeeIdFromInventory(coffeeType, condition, size, sugarLevel);

        if (coffeeid == null) {
            System.out.println("No matching coffee found in the inventory. Order cannot be placed.");
            return;
        }

        System.out.print("Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        

        //  Calculate totalPrice based on the price from inventory
        double totalPrice = calculateTotalPrice(coffeeid, quantity);

        Order order = new Order(coffeeType, condition, size, sugarLevel, coffeeid, quantity);
        order.setTotalPrice(totalPrice);


        // Update the order in the database
        updateOrderInDatabase(orderId, coffeeType, condition, size, sugarLevel, coffeeid ,quantity, totalPrice);

        updateInventoryStock(coffeeid, quantity, false);

        System.out.println("Order updated successfully! Updated order details:");
        displayOrderDetails(getOrderById(orderId));
    }

    private static void cancelOrder(Scanner scanner) throws SQLException {
        System.out.print("Enter the Order ID to cancel: ");
        int orderId = scanner.nextInt();
        scanner.nextLine(); 

        Order canceledOrder = getOrderById(orderId);
    
        if (canceledOrder == null) {
            System.out.println("Order not found with ID: " + orderId);
            return;
        }
    
        // Update inventory stock (increase stock for the canceled order)
        updateInventoryStock(canceledOrder.getCoffeeID(), canceledOrder.getQuantity(), true);
    
        cancelOrderFromDatabase(orderId);

        System.out.println("Order canceled successfully!");
    }
    
    private static void placeOrder(Scanner scanner) throws SQLException {
        try{
            System.out.print("Enter the Order ID to place: ");
            int orderId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Check if the order with the given ID exists
            Order existingOrder = getOrderById(orderId);
            if (existingOrder == null) {
                System.out.println("Order not found with ID: " + orderId);
                return;
            }

            // Retrieve the date when the order is placed
            LocalDate orderDate = LocalDate.now();

            // Check if a transaction already exists for the current date
            int existingTransactionId = getTransactionIdByDate(orderDate);

            if (existingTransactionId == -1) {
                // If no existing transaction for the current date, create a new transaction
                createNewTransaction(orderDate);
                existingTransactionId = getTransactionIdByDate(orderDate);
            }


            // Delete the order from the database
            placeOrderFromDatabase(orderId);
            updateTransactionValues(existingTransactionId, existingOrder.getTotalPrice(), existingOrder.getQuantity());
            


            System.out.println("Order placed successfully!");
            printOrderReceipt(existingOrder);
        }catch (SQLException e) {
            System.err.println("Error while placing an order: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void displayOrders() throws SQLException {
        System.out.println("All Orders:");
        for (Order order : getAllOrders()) {
            displayOrderDetails(order);
        }
    }

    private static boolean headerDisplayed = false;

    private static void displayHeader() {
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-10s | %-15s | %-10s | %-6s | %-13s | %-10s | %-8s | %-12s |\n",
            "Order ID", "Coffee Type", "Condition", "Size", "Sugar Level", "Coffee ID", "Quantity", "Total Price");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }

    private static void displayOrder(Order order) {
        System.out.printf("| %-10s | %-15s | %-10s | %-6s | %-13s | %-10s | %-8s | $%-10s  |\n",
            order.getId(), order.getCoffeeType(), order.getCondition(), order.getSize(),
            order.getSugarLevel(), order.getCoffeeID(), order.getQuantity(), order.getTotalPrice());
        System.out.println("-------------------------------------------------------------------------------------------------------------");
    }

    private static void displayOrderDetails(Order order) {

        if (!headerDisplayed) {
            displayHeader();
            headerDisplayed = true;
        }
        // Your order display code
        displayOrder(order);
    }

    public static void insertOrderIntoDatabase(Order order) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO order_table (coffee_type, `condition`, size, sugar_level, coffee_id, quantity, total_price) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, order.getCoffeeType());
                preparedStatement.setString(2, order.getCondition());
                preparedStatement.setString(3, order.getSize());
                preparedStatement.setInt(4, order.getSugarLevel());
                preparedStatement.setString(5, order.getCoffeeID());
                preparedStatement.setInt(6, order.getQuantity());
                preparedStatement.setDouble(7, order.getTotalPrice());

                preparedStatement.executeUpdate();

                // Retrieve the generated ID and set it in the Order object
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reassignOrderIds(connection);
                        order.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    public static void updateOrderInDatabase(int orderId, String coffeeType, String condition, String size, int sugarLevel, String coffeeid,int quantity, double totalPrice) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE order_table SET coffee_type = ?, `condition` = ?, size = ?, sugar_level = ?, coffee_id = ?, quantity = ?, total_price = ? WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, coffeeType);
                preparedStatement.setString(2, condition);
                preparedStatement.setString(3, size);
                preparedStatement.setInt(4, sugarLevel);
                preparedStatement.setString(5, coffeeid);
                preparedStatement.setInt(6, quantity);
                preparedStatement.setDouble(7, totalPrice);
                preparedStatement.setInt(8, orderId);

                preparedStatement.executeUpdate();

            }
             reassignOrderIds(connection);
        }
    }

    

    public static void placeOrderFromDatabase(int orderId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM order_table WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.executeUpdate();
            }
            // Reassign IDs after the delete
            reassignOrderIds(connection);
        }
    }

    public static void cancelOrderFromDatabase(int orderId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "DELETE FROM order_table WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, orderId);
                preparedStatement.executeUpdate();
            }
            // Reassign IDs after the delete
            reassignOrderIds(connection);
        }
    }

    public static List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM order_table";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Order order = new Order(
                                resultSet.getString("coffee_type"),
                                resultSet.getString("condition"),
                                resultSet.getString("size"),
                                resultSet.getInt("sugar_level"),
                                resultSet.getString("coffee_id"),
                                resultSet.getInt("quantity")
                        );
                        order.setId(resultSet.getInt("id"));
                        order.setTotalPrice(resultSet.getDouble("total_price"));
                        orders.add(order);
                    }
                }
            }
        }

        return orders;
    }

    public static Order getOrderById(int orderId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT * FROM order_table WHERE id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, orderId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // return new Order(
                            Order order = new Order(
                                resultSet.getString("coffee_type"),
                                resultSet.getString("condition"),
                                resultSet.getString("size"),
                                resultSet.getInt("sugar_level"),
                                resultSet.getString("coffee_id"),
                                resultSet.getInt("quantity")
                        );
                        order.setId(resultSet.getInt("id"));
                        order.setTotalPrice(resultSet.getDouble("total_price")); // Include total_price
                        return order;
                    }
                }
            }
        }
        return null;
    }

    public static void printOrderReceipt(Order order) {
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        
        System.out.println("╔═══════════════════════════╗");
        System.out.println("║      Order Receipt        ║");
        System.out.println("╠═══════════════════════════╣");
        System.out.printf("║ Order ID: %-14s ║\n", order.getId());
        System.out.printf("║ Coffee Type: %-11s   ║\n", order.getCoffeeType());
        System.out.printf("║ Condition: %-12s   ║\n", order.getCondition());
        System.out.printf("║ Size: %-17s   ║\n", order.getSize());
        System.out.printf("║ Sugar Level: %-12s   ║\n", order.getSugarLevel());
        System.out.printf("║ Quantity: %-15s   ║\n", order.getQuantity());
        System.out.printf("║ Total Price: $%-10s   ║\n", order.getTotalPrice());
        System.out.println("║                           ║");
        System.out.println("║        Thank You!        ║");
        System.out.println("╚═══════════════════════════╝");

        // System.out.println("Order Receipt:");
        // System.out.println("------------------------------");
        // System.out.println("Order ID: " + order.getId());
        // System.out.println("Coffee Type: " + order.getCoffeeType());
        // System.out.println("Condition: " + order.getCondition());
        // System.out.println("Size: " + order.getSize());
        // System.out.println("Sugar Level: " + order.getSugarLevel());
        // System.out.println("Quantity: " + order.getQuantity());
        // System.out.println("Total Price: $" + order.getTotalPrice());
        // System.out.println("------------------------------");
        // System.out.println("Coffee ID: " + order.getCoffeeID());
    }
    
    
    private static void reassignOrderIds(Connection connection) throws SQLException {
        String setRowNumberSql = "SET @row_number = 0;";
        String updateIdsSql = "UPDATE order_table SET id = (@row_number:=@row_number+1);";
        
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(setRowNumberSql);
            statement.executeUpdate(updateIdsSql);
        }
    }
    
    

    private static String getCoffeeIdFromInventory(String coffeeType, String condition, String size, int sugarLevel) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT CoffeeID FROM inventory_table WHERE type = ? AND `condition` = ? AND size = ? AND sugar_level = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, coffeeType);
                preparedStatement.setString(2, condition);
                preparedStatement.setString(3, size);
                preparedStatement.setInt(4, sugarLevel);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("CoffeeID");
                    }
                }
            }
        }
        return null;
    }

    private static double calculateTotalPrice(String coffeeId, int quantity) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT price FROM inventory_table WHERE CoffeeID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, coffeeId);
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        double price = resultSet.getDouble("price");
                        return price * quantity;
                    }
                }
            }
        }
        return 0.0;
    }


    private static void updateInventoryStock(String coffeeId, int quantity, boolean increaseStock) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql;
    
            if (increaseStock) {
                // Increase the stock
                sql = "UPDATE inventory_table SET stock = stock + ? WHERE CoffeeID = ?";
            } else {
                // Decrease the stock
                sql = "UPDATE inventory_table SET stock = stock - ? WHERE CoffeeID = ?";
            }
    
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, quantity);
                preparedStatement.setString(2, coffeeId);
    
                preparedStatement.executeUpdate();
            }
        }
    }

    private static void createNewTransaction(LocalDate date) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "INSERT INTO transaction_table (date, customer_perDay, revenue_perDay, quantity_sale_perDay) VALUES (?, 1, 0.0, 0)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setDate(1, Date.valueOf(date));
                preparedStatement.executeUpdate();
            }
        }
    }
    
    private static void updateTransactionValues(int transactionId, double totalPrice, int quantity) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "UPDATE transaction_table SET customer_perDay = customer_perDay + 1, revenue_perDay = revenue_perDay + ?, quantity_sale_perDay = quantity_sale_perDay + ? WHERE transactionID = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDouble(1, totalPrice);
                preparedStatement.setInt(2, quantity);
                preparedStatement.setInt(3, transactionId);
                preparedStatement.executeUpdate();
            }
        }
    }
    
    
    private static int getTransactionIdByDate(LocalDate date) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String sql = "SELECT transactionID FROM transaction_table WHERE date = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setDate(1, Date.valueOf(date));
    
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("transactionID");
                    }
                }
            }
        }
        return -1; // Return -1 if no transaction found for the given date
    }
}
