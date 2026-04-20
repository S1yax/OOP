package users;

import database.Database;
import patterns.UserFactory;

import java.util.List;
import java.util.Scanner;

public class Admin extends User {

    private static final long serialVersionUID = 1L;

    public Admin(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    @Override
    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        Database db = Database.getInstance();
        boolean running = true;

        while (running) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View all users");
            System.out.println("2. Add user");
            System.out.println("3. View system logs");
            System.out.println("4. View all courses");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    db.getAllUsers().forEach(System.out::println);
                    break;
                case "2":
                    addUserFlow(sc, db);
                    break;
                case "3":
                    List<String> logs = db.getLogs();
                    if (logs.isEmpty()) System.out.println("No logs yet.");
                    else logs.forEach(System.out::println);
                    break;
                case "4":
                    db.getCourses().forEach(System.out::println);
                    break;
                case "0":
                    running = false;
                    db.log("Admin [" + getLogin() + "] logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addUserFlow(Scanner sc, Database db) {
        System.out.print("Role (student/teacher/manager/researcher): ");
        String role = sc.nextLine().trim();
        System.out.print("Login: ");     String login = sc.nextLine().trim();
        System.out.print("Password: ");  String pass  = sc.nextLine().trim();
        System.out.print("First name: ");String fn    = sc.nextLine().trim();
        System.out.print("Last name: "); String ln    = sc.nextLine().trim();

        try {
            User u = UserFactory.createUser(role, login, pass, fn, ln);
            db.addUser(u);
            db.log("Admin created user: " + login + " (" + role + ")");
            System.out.println("User created: " + u);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}