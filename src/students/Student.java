package students;
import database.Course;
import database.Database;
import users.Mark;
import users.User;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
public class Student extends User {

    private static final long serialVersionUID = 1L;

    private String group;
    private int yearOfStudy;

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
            System.out.println("\n=== STUDENT MENU — " + getFullName() + " ===");
            System.out.println("1. View enrolled courses");
            System.out.println("2. View my grades");
            System.out.println("3. View GPA");
            System.out.println("4. View available courses");
            System.out.println("5. View schedule");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    List<Course> enrolled = getEnrolledCourses(db);
                    if (enrolled.isEmpty()) System.out.println("Not enrolled in any courses.");
                    else enrolled.forEach(System.out::println);
                    System.out.println("Total credits: " + getTotalCredits(db) + "/21");
                    break;
                case "2":
                    viewGrades(db);
                    break;
                case "3":
                    System.out.printf("GPA: %.2f%n", getGPA(db));
                    break;
                case "4":
                    db.getCourses().stream()
                            .filter(c -> !c.isEnrolled(this))
                            .forEach(System.out::println);
                    break;
                case "5":
                    viewSchedule(db);
                    break;
                case "0":
                    running = false;
                    db.log("Student [" + getLogin() + "] logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void viewGrades(Database db) {
        List<Course> courses = getEnrolledCourses(db);
        if (courses.isEmpty()) { System.out.println("No enrolled courses."); return; }
        for (Course c : courses) {
            Mark m = c.getMark(this);
            System.out.printf("  %-30s %s%n", c.getName(),
                    m == null ? "Not graded yet" : m.toString());
        }
    }

    private void viewSchedule(Database db) {
        getEnrolledCourses(db).forEach(c -> {
            System.out.println("Course: " + c.getName());
            c.getLessons().forEach(l -> System.out.println("   " + l));
        });
    }

    public String getGroup()          { return group; }
    public int getYearOfStudy()       { return yearOfStudy; }
    public void setYearOfStudy(int y) { this.yearOfStudy = y; }

    @Override
    public String toString() {
        return super.toString() + " | Group: " + group;
    }
}
