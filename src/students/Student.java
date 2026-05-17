
package students;

import communication.Message;
import database.Course;
import database.Database;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import research.ResearchTeacher;
import research.Researcher;
import research.ResearcherApplication;
import users.Mark;
import users.User;
//Нурасыл
public class Student extends User {

    private static final long serialVersionUID = 1L;

    private String group;
    private int    yearOfStudy;

    public Student(String login, String password, String firstName, String lastName, String group) {
        super(login, password, firstName, lastName);
        this.group       = group;
        this.yearOfStudy = 1;
    }

    public int getTotalCredits(Database db) {
        return db.getCourses().stream()
                .filter(c -> c.isEnrolled(this))
                .mapToInt(Course::getCredits)
                .sum();
    }

    public List<Course> getEnrolledCourses(Database db) {
        return db.getCourses().stream()
                .filter(c -> c.isEnrolled(this))
                .collect(Collectors.toList());
    }

    public double getGPA(Database db) {
        List<Course> courses = getEnrolledCourses(db);
        if (courses.isEmpty()) return 0.0;
        double total = 0;
        int count = 0;
        for (Course c : courses) {
            Mark m = c.getMark(this);
            if (m != null) { total += m.getTotal(); count++; }
        }
        return count == 0 ? 0.0 : total / count;
    }

    @Override
    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        Database db = Database.getInstance();
        boolean running = true;

        while (running) {
            System.out.println("\n=== STUDENT MENU — " + getFullName() + " [Year " + yearOfStudy + "] ===");
            System.out.println("1.  My courses");
            System.out.println("2.  My grades");
            System.out.println("3.  My GPA");
            System.out.println("4.  Available courses / enroll");
            System.out.println("5.  Schedule");
            System.out.println("6.  Transcript");
            System.out.println("7.  Rate a teacher");
            System.out.println("8.  Send message");
            System.out.println("9.  Inbox");
            System.out.println("10. Apply for researcher status");

            if (yearOfStudy >= 4) {
                System.out.println("11. My mentor (ResearchTeacher)");
            }

            System.out.println("0.  Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    List<Course> enrolled = getEnrolledCourses(db);
                    if (enrolled.isEmpty()) System.out.println("Not enrolled in any courses.");
                    else enrolled.forEach(System.out::println);
                    System.out.println("Credits: " + getTotalCredits(db) + "/21");
                    break;
                case "2":
                    viewGrades(db);
                    break;
                case "3":
                    printGPA(db);
                    break;
                case "4":
                    registerCourse(sc, db);
                    break;
                case "5":
                    viewSchedule(db);
                    break;
                case "6":
                    viewTranscript(db);
                    break;
                case "7":
                    rateTeacher(sc, db);
                    break;
                case "8":
                    sendMessage(sc, db);
                    break;
                case "9":
                    viewInbox(db);
                    break;
                case "10":
                    applyForResearcher(sc, db);
                    break;
                case "11":
                    if (yearOfStudy >= 4) viewMentor(db);
                    else System.out.println("Invalid choice.");
                    break;
                case "0":
                    running = false;
                    db.log("Student [" + getLogin() + "] logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewGrades(Database db) {
        List<Course> courses = getEnrolledCourses(db);
        if (courses.isEmpty()) { System.out.println("Not enrolled in any courses."); return; }
        System.out.println("\nMY GRADES");
        boolean anyGrade = false;
        for (Course c : courses) {
            Mark m = c.getMark(this);
            if (m != null) {
                System.out.printf("  %-35s %s%n", c.getName(), m);
                anyGrade = true;
            } else {
                System.out.printf("  %-35s %s%n", c.getName(), "Not graded yet");
            }
        }
        if (!anyGrade) System.out.println("No grades have been assigned yet.");
    }

    private void printGPA(Database db) {
        double gpa = getGPA(db);
        if (gpa == 0.0) {
            System.out.println("GPA: 0.00  (no grades recorded yet)");
        } else {
            System.out.printf("GPA: %.2f%n", gpa);
            String letter = gpa >= 90 ? "A" : gpa >= 80 ? "B" : gpa >= 70 ? "C" : gpa >= 60 ? "D" : "F";
            System.out.println("Letter grade: " + letter);
        }
    }

    private void viewTranscript(Database db) {
        System.out.println("\n_____________TRANSCRIPT_____________");
        System.out.println("Student : " + getFullName());
        System.out.println("Group   : " + group);
        System.out.println("Year    : " + yearOfStudy);
        System.out.println("______________________________-");
        List<Course> courses = getEnrolledCourses(db);
        if (courses.isEmpty()) { System.out.println("No courses found."); return; }
        for (Course c : courses) {
            Mark m = c.getMark(this);
            System.out.printf("  %-35s %s%n", c.getName(),
                    m == null ? "Not graded" : m.toString());
        }
        System.out.println("_____________________________");
        System.out.printf("GPA     : %.2f%n", getGPA(db));
        System.out.println("Credits : " + getTotalCredits(db) + "/21");
        System.out.println("________________________________");
    }

    private void registerCourse(Scanner sc, Database db) {
        List<Course> available = db.getCourses().stream()
                .filter(c -> !c.isEnrolled(this))
                .collect(Collectors.toList());
        if (available.isEmpty()) { System.out.println("No available courses."); return; }
        available.forEach(System.out::println);
        System.out.print("Enter course ID: ");
        String id = sc.nextLine().trim();
        Course c = db.getCourseById(id);
        if (c == null) { System.out.println("Course not found."); return; }
        if (c.isEnrolled(this)) { System.out.println("Already enrolled."); return; }
        int current = getTotalCredits(db);
        if (current + c.getCredits() > 21) {
            System.out.printf("Credit limit exceeded (%d + %d > 21).%n", current, c.getCredits());
            return;
        }
        c.enroll(this);
        db.log("Student [" + getLogin() + "] enrolled in " + id);
        System.out.println("Enrolled! Credits: " + (current + c.getCredits()) + "/21");
    }

    private void viewSchedule(Database db) {
        getEnrolledCourses(db).forEach(c -> {
            System.out.println("Course: " + c.getName());
            if (c.getLessons().isEmpty()) System.out.println("   No lessons scheduled.");
            else c.getLessons().forEach(l -> System.out.println("   " + l));
        });
    }

    private void rateTeacher(Scanner sc, Database db) {
        System.out.print("Teacher login: ");
        String tLogin = sc.nextLine().trim();
        User u = db.getUserByLogin(tLogin);
        if (u == null) { System.out.println("User not found."); return; }
        System.out.print("Rating (1-5): ");
        try {
            int rating = Integer.parseInt(sc.nextLine().trim());
            if (rating < 1 || rating > 5) { System.out.println("Rating must be between 1 and 5."); return; }
            System.out.print("Comment: ");
            String comment = sc.nextLine().trim();
            db.log("Student [" + getLogin() + "] rated Teacher [" + tLogin + "]: " + rating + "/5 — " + comment);
            System.out.println("Rating submitted: " + rating + "/5");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
        }
    }

    private void sendMessage(Scanner sc, Database db) {
        System.out.print("Recipient login: ");
        String to = sc.nextLine().trim();
        if (db.getUserByLogin(to) == null) { System.out.println("User not found."); return; }
        System.out.print("Subject: ");
        String subject = sc.nextLine().trim();
        System.out.print("Message: ");
        String body = sc.nextLine().trim();
        db.sendMessage(new Message(getLogin(), getFullName(), to, subject, body, Message.Type.REGULAR));
        System.out.println("Message sent.");
    }

    private void viewInbox(Database db) {
        List<Message> inbox = db.getMessagesFor(getLogin());
        if (inbox.isEmpty()) { System.out.println("Inbox is empty."); return; }
        inbox.forEach(m -> System.out.println("\n" + m));
    }

    private void applyForResearcher(Scanner sc, Database db) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║       RESEARCHER STATUS APPLICATION          ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Approval criteria:                          ║");
        System.out.println("║  • At least " + ResearcherApplication.MIN_PAPERS + " published papers              ║");
        System.out.println("║  • Research interests provided               ║");
        System.out.println("║  • Affiliation (dept./org.) provided         ║");
        System.out.println("║  • Project idea described                    ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        ResearcherApplication existing = db.getApplicationByLogin(getLogin());
        if (existing != null) {
            System.out.println("Your latest application: " + existing);
            if (existing.getStatus() == ResearcherApplication.Status.PENDING) {
                System.out.println("⏳ Application is already under review. Please wait.");
                return;
            }
            if (existing.getStatus() == ResearcherApplication.Status.APPROVED) {
                System.out.println("✅ Your application has already been approved! Contact the admin to update your role.");
                return;
            }
            System.out.println("❌ Previous application rejected: " + existing.getReviewerComment());
            System.out.print("Submit a new application? (yes/no): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("yes")) return;
        }

        System.out.println("\nFill in the application:");

        System.out.print("Research interests (field of study): ");
        String interests = sc.nextLine().trim();
        if (interests.isBlank()) { System.out.println("This field is required."); return; }

        System.out.print("Affiliation (department / organization): ");
        String affiliation = sc.nextLine().trim();
        if (affiliation.isBlank()) { System.out.println("This field is required."); return; }

        System.out.print("Project idea (briefly): ");
        String idea = sc.nextLine().trim();
        if (idea.isBlank()) { System.out.println("This field is required."); return; }

        System.out.print("Number of published papers: ");
        int papers;
        try {
            papers = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid format."); return;
        }

        ResearcherApplication app = new ResearcherApplication(
                getLogin(), getFullName(), interests, affiliation, idea, papers);

        if (!app.meetsMinimumCriteria()) {
            System.out.println("\n⚠️  Application does not meet minimum criteria:");
            if (papers < ResearcherApplication.MIN_PAPERS)
                System.out.println("   — At least " + ResearcherApplication.MIN_PAPERS + " published papers required (you have: " + papers + ")");
            System.out.println("   The application will still be submitted but will likely be rejected.");
        }

        db.submitApplication(app);
        System.out.println("\n✅ Application submitted! Awaiting review by admin or manager.");
        System.out.println("Status: " + app.getStatus());
    }

    private void viewMentor(Database db) {
        ResearchTeacher mentor = db.getMentor(getLogin());
        if (mentor == null) {
            System.out.println("No mentor assigned yet. Please contact the manager.");
            return;
        }
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║           YOUR MENTOR                ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("Name: " + mentor.getFullName());
        System.out.println("Login: " + mentor.getLogin());
        System.out.println("h-index: " + mentor.calculateHIndex());
        System.out.println("Paper: " + mentor.getPapers().size());
        if (!mentor.getPapers().isEmpty()) {
            System.out.println("  Top publications:");
            mentor.getPapers().stream()
                  .sorted(Researcher.byCitations())
                  .limit(3)
                  .forEach(p -> System.out.println(" • " + p));
        }
        System.out.println("\n  To contact: Send message → login: " + mentor.getLogin());
    }

    public String getGroup(){ 
        return group;
     }
    public int getYearOfStudy() { 
        return yearOfStudy;
     }
    public void setYearOfStudy(int y) {
         this.yearOfStudy = y; 
        }

    @Override
    public String toString() {
        return super.toString() + " | Group: " + group + " | Year: " + yearOfStudy;
    }
}