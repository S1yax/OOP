package users;

import database.Database;
import java.util.List;
import java.util.Scanner;
import patterns.UserFactory;
import research.ResearchTeacher;
import research.ResearcherApplication;
import students.Student;
//Сания
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
            System.out.println("1.  All users");
            System.out.println("2.  Add user");
            System.out.println("3.  Remove user");
            System.out.println("4.  Change password");
            System.out.println("5.  System logs");
            System.out.println("6.  All courses");
            System.out.println("7.  All messages");
            System.out.println("8.  Researcher status applications");
            System.out.println("9.  Process application (→ ResearchTeacher)");
            System.out.println("10. Assign mentor to 4th-year student");
            System.out.println("0.  Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    db.getAllUsers().forEach(System.out::println);
                    break;
                case "2":
                    addUserFlow(sc, db);
                    break;
                case "3":
                    removeUserFlow(sc, db);
                    break;
                case "4":
                    changePasswordFlow(sc, db);
                    break;
                case "5":
                    List<String> logs = db.getLogs();
                    if (logs.isEmpty()) System.out.println("No logs yet.");
                    else logs.forEach(System.out::println);
                    break;
                case "6":
                    db.getCourses().forEach(System.out::println);
                    break;
                case "7":
                    db.getAllMessages().forEach(m -> System.out.println("\n" + m));
                    break;
                case "8":
                    viewApplications(db);
                    break;
                case "9":
                    processApplication(sc, db);
                    break;
                case "10":
                    assignMentor(sc, db);
                    break;
                case "0":
                    running = false;
                    db.log("Admin [" + getLogin() + "] logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void addUserFlow(Scanner sc, Database db) {
        System.out.print("Role (student/teacher/manager/researcher/admin): ");
        String role = sc.nextLine().trim();
        System.out.print("Login: "); String login = sc.nextLine().trim();
        System.out.print("Password: "); String pass  = sc.nextLine().trim();
        System.out.print("First name: "); String fn    = sc.nextLine().trim();
        System.out.print("Last name: ");String ln    = sc.nextLine().trim();
        try {
            User u = UserFactory.createUser(role, login, pass, fn, ln);
            db.addUser(u);
            db.log("Admin created user: " + login + " (" + role + ")");
            System.out.println("✅ User created: " + u);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void removeUserFlow(Scanner sc, Database db) {
        System.out.print("Login to remove: ");
        String login = sc.nextLine().trim();
        if (db.getUserByLogin(login) == null) { System.out.println("User not found."); return; }
        db.removeUser(login);
        db.log("Admin removed user: " + login);
        System.out.println("✅ User removed.");
    }

    private void changePasswordFlow(Scanner sc, Database db) {
        System.out.print("User login: ");
        String login = sc.nextLine().trim();
        User u = db.getUserByLogin(login);
        if (u == null) { System.out.println("User not found."); return; }
        System.out.print("New password: ");
        u.setPassword(sc.nextLine().trim());
        db.log("Admin changed password for: " + login);
        System.out.println("✅ Password updated.");
    }

    private void viewApplications(Database db) {
        List<ResearcherApplication> apps = db.getAllApplications();
        if (apps.isEmpty()) { System.out.println("No applications found."); return; }
        System.out.println("\n--- ALL RESEARCHER APPLICATIONS ---");
        apps.forEach(a -> {
            System.out.println("\n  " + a);
            System.out.println("  Interests  : " + a.getResearchInterests());
            System.out.println("  Affiliation: " + a.getAffiliation());
            System.out.println("  Idea       : " + a.getProjectIdea());
            System.out.println("  Min. criteria: " + (a.meetsMinimumCriteria() ? "✅" : "❌"));
            if (a.getReviewerComment() != null)
                System.out.println("  Comment: " + a.getReviewerComment());
        });
    }

    //Approves an application and automatically upgrades the user to ResearchTeacher.
    private void processApplication(Scanner sc, Database db) {
        List<ResearcherApplication> pending = db.getPendingApplications();
        if (pending.isEmpty()) { System.out.println("No pending applications."); return; }

        System.out.println("\n--- PENDING APPLICATIONS ---");
        for (int i = 0; i < pending.size(); i++) {
            ResearcherApplication app = pending.get(i);
            System.out.println("[" + (i+1) + "] " + app
                    + " | Min. criteria: " + (app.meetsMinimumCriteria() ? "✅" : "❌"));
            System.out.println("    Interests: " + app.getResearchInterests()
                    + " | Papers: " + app.getExistingPapers());
        }

        System.out.print("Applicant login to process: ");
        String login = sc.nextLine().trim();
        ResearcherApplication app = db.getApplicationByLogin(login);
        if (app == null || app.getStatus() != ResearcherApplication.Status.PENDING) {
            System.out.println("Application not found or already processed."); return;
        }

        System.out.println("Application: " + app);
        System.out.println("Min. criteria: " + (app.meetsMinimumCriteria() ? "✅ Met" : "❌ Not met"));
        System.out.print("Action (1=Approve / 2=Reject): ");
        String action = sc.nextLine().trim();
        System.out.print("Comment: ");
        String comment = sc.nextLine().trim();

        if (action.equals("1")) {
            app.approve(comment);
            User existing = db.getUserByLogin(login);
            if (existing != null && !(existing instanceof ResearchTeacher)) {
                ResearchTeacher rt = new ResearchTeacher(
                        existing.getLogin(), existing.getPassword(),
                        existing.getFirstName(), existing.getLastName());
                db.addUser(rt);
                db.log("Admin upgraded [" + login + "] to ResearchTeacher");
                System.out.println("✅ Application approved. User upgraded to ResearchTeacher.");
            } else {
                System.out.println("✅ Application approved.");
            }
        } else if (action.equals("2")) {
            app.reject(comment);
            db.log("Admin rejected application: " + login);
            System.out.println("❌ Application rejected.");
        } else {
            System.out.println("Unrecognized action.");
        }
    }

    //Assigns a ResearchTeacher as a mentor to a 4th-year (or above) student. Each student can have only one mentor, but a ResearchTeacher can mentor multiple students.
    private void assignMentor(Scanner sc, Database db) {
        System.out.println("\n--- ASSIGN MENTOR TO 4TH-YEAR STUDENT ---");
        System.out.println("4th+ year students:");
        db.getAllUsers().stream()
                .filter(u -> u instanceof Student && ((Student) u).getYearOfStudy() >= 4)
                .forEach(u -> {
                    Student st = (Student) u;
                    String mentor = db.getMentorLogin(st.getLogin());
                    System.out.printf("  %-12s | %s | Mentor: %s%n",
                            st.getLogin(), st.getFullName(),
                            mentor != null ? mentor : "—");
                });

        System.out.print("\nStudent login: ");
        String sLogin = sc.nextLine().trim();
        User su = db.getUserByLogin(sLogin);
        if (!(su instanceof Student)) { System.out.println("Student not found."); return; }
        if (((Student) su).getYearOfStudy() < 4) {
            System.out.println("Mentors can only be assigned to 4th-year students and above.");
            return;
        }

        System.out.println("Available ResearchTeachers:");
        List<ResearchTeacher> rts = db.getResearchTeachers();
        if (rts.isEmpty()) { System.out.println("No ResearchTeachers in the system."); return; }
        rts.forEach(rt -> System.out.printf("  %-12s | %s | h-index: %d%n",
                rt.getLogin(), rt.getFullName(), rt.calculateHIndex()));

        System.out.print("Mentor login: ");
        String mLogin = sc.nextLine().trim();
        User mu = db.getUserByLogin(mLogin);
        if (!(mu instanceof ResearchTeacher)) {
            System.out.println("This user is not a ResearchTeacher."); return;
        }
        db.setMentor(sLogin, mLogin);
        System.out.println("✅ Mentor assigned: " + mu.getFullName() + " → " + su.getFullName());
    }
}