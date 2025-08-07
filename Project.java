import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
public class Project {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/inventor";
    private static final String USER = "root";   
    private static final String PASS = "root";

    private Connection connection;
    private Scanner scanner;
    public Project(){
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");
            scanner = new Scanner(System.in);
        } catch (SQLException se) {
            se.printStackTrace();
            System.err.println("Database connection failed. Check your DB_URL, USER, PASS, and ensure MySQL is running.");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println("JDBC Driver not found. Make sure mysql-connector-j-x.x.x.jar is in your classpath.");
        }
    }
     public void start() {
        if (connection == null) {
            System.err.println("Application cannot start without a database connection.");
            return;
        }

        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addData();
                    break;
                case 2:
                    viewAllData();
                    break;
                case 3: 
                    updateData();
                    break;
                case 4:
                    deleteData();
                    break;
                case 5:
                    System.out.println("Exiting Inventor Application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n------------------------------------\n");
        } while (choice != 5);

        closeResources();
    }
     private void displayMenu() {
        System.out.println("--- Simple Inventor Application ---");
        System.out.println("1. Add Data");
        System.out.println("2. View All Datas");
        System.out.println("3. Update Data Quantity/Price");
        System.out.println("4. Delete Data");
        System.out.println("5. Exit");
    }
   private void addData() {
        System.out.println("\n--- Add New Data ---");
        System.out.print("Enter Data Id: ");
        int id=scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Data name: ");
        String name = scanner.nextLine();
        scanner.nextLine();

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        String sql = "INSERT INTO Data (Id, Name, Quantity, Price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, price);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Data " + name + " added successfully.");
            } else {
                System.out.println("Failed to add data.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    private void viewAllData() {
        System.out.println("\n--- All Data in Inventor ---");
        String sql = "SELECT Id, Name, Quantity, Price FROM Data";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No Data found in inventor.");
                return;
            }

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                System.out.printf("ID: %d, Name: %s, Quantity: %d, Price: %.2f%n", id, name, quantity, price);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    private void updateData() {
        System.out.println("\n--- Update Data ---");
        System.out.print("Enter Data ID to update: ");
        int id = scanner.nextInt();

        System.out.print("Enter new Name: ");
        String newName=scanner.nextLine();
        scanner.nextLine();

        System.out.print("Enter new quantity: ");
        int newQuantity = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new price: ");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE data SET name= ?, quantity = ?, price = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newQuantity);
            pstmt.setDouble(3, newPrice);
            pstmt.setInt(4, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Data with ID " + id + " updated successfully.");
            } else {
                System.out.println("No Data found with ID " + id + " to update.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
   private void deleteData() {
        System.out.println("\n--- Delete Data ---");
        System.out.print("Enter data ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM data WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Data with ID " + id + " deleted successfully.");
            } else {
                System.out.println("No data found with ID " + id + " to delete.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    @SuppressWarnings("CallToPrintStackTrace")
   private void closeResources() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Database connection closed.");
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Project app = new Project();
          app.start();
    }
}