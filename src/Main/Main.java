package Main;


import communication.News;
import communication.NewsBoard;
import database.Database;
import research.ResearchPaper;
import research.Researcher;
import users.User;
import utils.InputUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Main {

    private static final NewsBoard newsBoard = new NewsBoard();

    public static void main(String[] args) {
        Database db = Database.getInstance();
        db.seedDemoData();

        printWelcomeBanner();
        postInitialNews(db);

        boolean appRunning = true;
        while (appRunning) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   UNIVERSITY INFORMATION SYSTEM      ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Login                            ║");
            System.out.println("║  2. View news board                  ║");
            System.out.println("║  3. View top researcher              ║");
            System.out.println("║  0. Exit                             ║");
            System.out.println("╚══════════════════════════════════════╝");

            String choice = InputUtils.readLine("Choice: ");
            switch (choice) {
                case "1" -> loginFlow(db);
                case "2" -> newsBoard.printAll();
                case "3" -> showTopResearcher(db);
                case "0" -> {
                    db.save();
                    System.out.println("Goodbye! Data saved.");
                    appRunning = false;
                }
                default  -> System.out.println("Invalid option.");
            }
        }
    }

    // ── Login ──────────────────────────────────────────────
    private static void loginFlow(Database db) {
        System.out.println("\n--- LOGIN ---");
        String login    = InputUtils.readLine("Login:    ");
        String password = InputUtils.readLine("Password: ");

        User user = db.authenticate(login, password);
        if (user == null) {
            System.out.println("❌ Invalid credentials. Please try again.");
            db.log("Failed login attempt for: " + login);
            return;
        }

        System.out.println("✓ Welcome, " + user.getFullName() + "! (" + user.getClass().getSimpleName() + ")");
        db.log("User [" + login + "] logged in as " + user.getClass().getSimpleName());

        // Polymorphic dispatch — each user type has its own menu
        user.showMenu();
    }

    // ── Top Researcher ─────────────────────────────────────
    private static void showTopResearcher(Database db) {
        Optional<User> top = db.getAllUsers().stream()
                .filter(u -> u instanceof Researcher)
                .max(Comparator.comparingInt(u -> ((Researcher) u).calculateHIndex()));

        top.ifPresentOrElse(u -> {
            int h = ((Researcher) u).calculateHIndex();
            System.out.println("\n🏆 Top Researcher: " + u.getFullName() + " | h-index: " + h);
            newsBoard.postTopCitedResearcher(u.getFullName(), h);

            System.out.println("Papers (sorted by citations):");
            ((Researcher) u).printPapers(Researcher.byCitations());
        }, () -> System.out.println("No researchers found in the system."));
    }

    // ── Helpers ────────────────────────────────────────────
    private static void postInitialNews(Database db) {
        newsBoard.post(new News(
                "Welcome to the University System",
                "The new academic year has started. Please check your course registrations.",
                "Administration"
        ));
        newsBoard.post(new News(
                "Credit Limit Reminder",
                "Students may register for a maximum of 21 credits per semester.",
                "Academic Office"
        ));
    }

    private static void printWelcomeBanner() {
        System.out.println("""
                ╔══════════════════════════════════════════════╗
                ║     UNIVERSITY INFORMATION SYSTEM v1.0       ║
                ║     OOP Project — Java                       ║
                ╚══════════════════════════════════════════════╝
                  Default accounts:
                    admin     / admin123
                    jsmith    / pass123  (Teacher)
                    adoe      / pass123  (Teacher)
                    bob       / pass123  (Student)
                    eva       / pass123  (Student)
                    carol     / pass123  (Manager)
                """);
    }
}


