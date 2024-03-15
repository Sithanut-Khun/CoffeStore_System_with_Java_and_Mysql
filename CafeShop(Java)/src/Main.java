import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AccountMainSystem();
    }
    public static void AccountMainSystem() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                if (Account.isLoggedIn()) {
                    System.out.println("\n╭──────────────────────────────────────╮");
                    System.out.println("│        Welcome to Your Cafe          │");
                    System.out.println("├──────────────────────────────────────┤");
                    System.out.println("│         ♨  Explore Options ♨         │");
                    System.out.println("│  1. Order Menu                       │");
                    System.out.println("│  2. Inventory                        │");
                    System.out.println("│  3. Transaction System               │");
                    System.out.println("│  4. Logout                           │");
                    System.out.println("│  5. Change Password                  │");
                    System.out.println("│  6. Exit                             │");
                    System.out.println("╰──────────────────────────────────────╯");
                    System.out.print("Enter your choice (1-6): ");

                    int option = scanner.nextInt();

                    switch (option) {
                        case 1:
                            OrderMenu.displayMenu(scanner);
                            break;
                        case 2:
                            Inventory.displayMenu(scanner);
                            break;
                        case 3:
                            TransactionSystem.displayMenu(scanner);
                            break;
                        case 4:
                            Account.logout();
                            break;
                        case 5:
                            Account.changePassword(scanner);
                            break;
                        case 6:
                            System.out.println("Exiting system. Goodbye!");
                            System.exit(0);
                        default:
                            System.out.println("Invalid option. Please enter a number between 1 and 6.");
                    }
                } else {
                    System.out.println("\n╭──────────────────────╮");
                    System.out.println("│      Login System    │");
                    System.out.println("├──────────────────────┤");
                    System.out.println("│  1. Login            │");
                    System.out.println("│  2. Sign Up          │");
                    System.out.println("│  3. Exit             │");
                    System.out.println("╰──────────────────────╯");
                    System.out.print("Enter your choice (1-3): ");
                    int option = scanner.nextInt();

                    switch (option) {
                        case 1:
                            Account.login(scanner);
                            break;
                        case 2:
                            Account.signUp(scanner);
                            break;
                        case 3:
                            System.out.println("Exiting system. Goodbye!");
                            System.exit(0);
                        default:
                            System.out.println("Invalid option. Please enter a number between 1 and 3.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
