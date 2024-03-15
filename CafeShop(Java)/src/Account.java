import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Account {
    private static final String DATABASE_URL = "jdbc:mysql://Your_localhost/Your_Schema?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DATABASE_USER = "Your Username";
    private static final String DATABASE_PASSWORD = "Your Password";
    private static String loggedInUser = null;

    public static void signUp(Scanner scanner) {
        System.out.print("Enter a new username: ");
        String username = scanner.next();
        System.out.print("Enter a password: ");
        String password = scanner.next();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            // Check if the username already exists
            try (PreparedStatement checkStatement = connection.prepareStatement("SELECT * FROM account_table WHERE username = ?")) {
                checkStatement.setString(1, username);
                ResultSet resultSet = checkStatement.executeQuery();
    
                if (resultSet.next()) {
                    System.out.println("Username already exists. Please choose a different username.");
                    return;
                }
            }
    
            // If username doesn't exist, proceed with sign up
            try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO account_table VALUES (?, ?)")) {
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.executeUpdate();
                System.out.println("Sign up successful!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Sign up failed. An error occurred.");
        }
    }

    public static void login(Scanner scanner) {
        System.out.print("Enter your username: ");
        String username = scanner.next();
        System.out.print("Enter your password: ");
        String password = scanner.next();

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM account_table WHERE username = ?;")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next() && resultSet.getString("password").equals(password)) {
                loggedInUser = username;
                System.out.println("Login successful! Welcome, " + loggedInUser + "!");
            } else {
                System.out.println("Invalid username or password. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        loggedInUser = null;
        System.out.println("Logout successful!");
    }

    private static void changeDatabasePassword(String username, String newPassword) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("UPDATE account_table SET password = ? WHERE username = ?")) {
            statement.setString(1, newPassword);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    public static void changePassword(Scanner scanner) {
        if (loggedInUser == null) {
            System.out.println("You need to be logged in to change your password.");
            return;
        }
    
        System.out.print("Enter your current password: ");
        String currentPassword = scanner.next();
    
        try {
            if (isPasswordCorrect(loggedInUser, currentPassword)) {
                System.out.print("Enter your new password: ");
                String newPassword = scanner.next();
    
                // Update the password in the database
                changeDatabasePassword(loggedInUser, newPassword);
    
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Incorrect current password. Password change failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error changing password. Please try again later.");
            e.printStackTrace();
        }
    }
    
    private static boolean isPasswordCorrect(String username, String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT password FROM account_table WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getString("password").equals(password);
        }
    }    


    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }
}

