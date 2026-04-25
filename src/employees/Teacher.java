package employees;
import database.Course;
import database.Database;
import students.Student;
import users.Mark;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Teacher extends Employee {

    private static final long serialVersionUID = 1L;

    public Teacher(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    public List<Course> getMyCourses() {
        return Database.getInstance().getCourses().stream()
                .filter(c -> c.getInstructor().getLogin().equals(this.getLogin()))
                .collect(Collectors.toList());
    }
    @Override
    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        Database db = Database.getInstance();
        boolean running = true;
        while (running) {
            System.out.println("\n=== TEACHER MENU — " + getFullName() + " ===");
            System.out.println("1. View my courses");
            System.out.println("2. View students in a course");
            System.out.println("3. Set grade for a student");
            System.out.println("4. View all grades for a course");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    List<Course> mine = getMyCourses();
                    if (mine.isEmpty()) System.out.println("No courses assigned.");
                    else mine.forEach(System.out::println);
                    break;
                case "2":
                    viewStudents(sc);
                    break;
                case "3":
                    setGrade(sc, db);
                    break;
                case "4":
                    viewGrades(sc);
                    break;
                case "0":
                    running = false;
                    db.log("Teacher [" + getLogin() + "] logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void viewStudents(Scanner sc) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        List<Student> students = c.getEnrolledStudents();
        if (students.isEmpty()) System.out.println("No students enrolled.");
        else students.forEach(s -> System.out.println("  " + s.getFullName()));
    }

    private void setGrade(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;

        System.out.print("Student login: ");
        String login = sc.nextLine().trim();
        users.User u = db.getUserByLogin(login);
        if (!(u instanceof Student)) {
            System.out.println("Student not found or not enrolled.");
            return;
        }
        Student st = (Student) u;
        if (!c.isEnrolled(st)) {
            System.out.println("Student is not enrolled in this course.");
            return;
        }

        Mark m = c.getMark(st);
        if (m == null) m = new Mark();

        System.out.print("Attestation 1 (0-30): ");
        m.setAttestation1(Double.parseDouble(sc.nextLine().trim()));
        System.out.print("Attestation 2 (0-30): ");
        m.setAttestation2(Double.parseDouble(sc.nextLine().trim()));
        System.out.print("Final exam (0-40): ");
        m.setFinalExam(Double.parseDouble(sc.nextLine().trim()));

        c.putMark(st, m);
        db.log("Teacher [" + getLogin() + "] graded " + login + " in " + c.getId() + ": " + m);
        System.out.println("Grade saved: " + m);
    }

    private void viewGrades(Scanner sc) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        if (c.getAllMarks().isEmpty()) {
            System.out.println("No grades recorded.");
            return;
        }
        c.getAllMarks().forEach((s, m) ->
                System.out.println("  " + s.getFullName() + ": " + m));
    }

    private Course selectMyCourse(Scanner sc) {
        List<Course> mine = getMyCourses();
        if (mine.isEmpty()) { System.out.println("You have no courses."); return null; }
        mine.forEach(c -> System.out.println("  " + c));
        System.out.print("Enter course ID: ");
        String id = sc.nextLine().trim();
        Course c = Database.getInstance().getCourseById(id);
        if (c == null || !c.getInstructor().getLogin().equals(this.getLogin())) {
            System.out.println("Course not found or not yours.");
            return null;
        }
        return c;
    }
}