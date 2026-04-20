package employees;

import database.Course;
import database.Database;
import database.Lesson;
import students.Student;
import users.User;

import java.util.Scanner;

public class Manager extends Employee {

    private static final long serialVersionUID = 1L;

    public Manager(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }

    @Override
    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        Database db = Database.getInstance();
        boolean running = true;

        while (running) {
            System.out.println("\n=== MANAGER MENU — " + getFullName() + " ===");
            System.out.println("1. View all courses");
            System.out.println("2. Add new course");
            System.out.println("3. Add lesson to course");
            System.out.println("4. Enroll student in course");
            System.out.println("5. Remove student from course");
            System.out.println("0. Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    db.getCourses().forEach(System.out::println);
                    break;
                case "2":
                    addCourse(sc, db);
                    break;
                case "3":
                    addLesson(sc, db);
                    break;
                case "4":
                    enrollStudent(sc, db);
                    break;
                case "5":
                    removeStudent(sc, db);
                    break;
                case "0":
                    running = false;
                    db.log("Manager [" + getLogin() + "] logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addCourse(Scanner sc, Database db) {
        System.out.print("Course ID: ");     String id     = sc.nextLine().trim();
        System.out.print("Course name: ");   String name   = sc.nextLine().trim();
        System.out.print("Credits: ");       int credits   = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Teacher login: "); String tLogin = sc.nextLine().trim();

        User u = db.getUserByLogin(tLogin);
        if (!(u instanceof Teacher)) { System.out.println("Teacher not found."); return; }
        Course c = new Course(id, name, credits, (Teacher) u);
        db.addCourse(c);
        db.log("Manager added course: " + id);
        System.out.println("Course added: " + c);
    }

    private void addLesson(Scanner sc, Database db) {
        System.out.print("Course ID: "); String cid = sc.nextLine().trim();
        Course c = db.getCourseById(cid);
        if (c == null) { System.out.println("Course not found."); return; }
        System.out.print("Subject: ");                        String sub  = sc.nextLine().trim();
        System.out.print("Type (LECTURE/SEMINAR/LAB): ");
        Lesson.LessonType t = Lesson.LessonType.valueOf(sc.nextLine().trim().toUpperCase());
        System.out.print("Day of week: ");                    String day  = sc.nextLine().trim();
        System.out.print("Time (e.g. 09:00): ");              String time = sc.nextLine().trim();
        System.out.print("Room: ");                           String room = sc.nextLine().trim();
        c.addLesson(new Lesson(sub, t, day, time, room));
        System.out.println("Lesson added.");
    }

    private void enrollStudent(Scanner sc, Database db) {
        System.out.print("Student login: "); String sLogin = sc.nextLine().trim();
        System.out.print("Course ID: ");     String cid    = sc.nextLine().trim();

        User u  = db.getUserByLogin(sLogin);
        Course c = db.getCourseById(cid);

        if (!(u instanceof Student) || c == null) {
            System.out.println("Student or course not found.");
            return;
        }
        Student s = (Student) u;
        int currentCredits = s.getTotalCredits(db);
        if (currentCredits + c.getCredits() > 21) {
            System.out.printf("Cannot enroll: credit limit exceeded (%d + %d > 21).%n",
                    currentCredits, c.getCredits());
            return;
        }
        if (c.enroll(s)) {
            db.log("Manager enrolled " + sLogin + " in " + cid);
            System.out.println("Enrolled. Credits used: " + (currentCredits + c.getCredits()) + "/21");
        } else {
            System.out.println("Student already enrolled.");
        }
    }

    private void removeStudent(Scanner sc, Database db) {
        System.out.print("Student login: "); String sLogin = sc.nextLine().trim();
        System.out.print("Course ID: ");     String cid    = sc.nextLine().trim();

        User u  = db.getUserByLogin(sLogin);
        Course c = db.getCourseById(cid);

        if (!(u instanceof Student) || c == null) {
            System.out.println("Student or course not found.");
            return;
        }
        if (c.unenroll((Student) u)) {
            db.log("Manager removed " + sLogin + " from " + cid);
            System.out.println("Unenrolled successfully.");
        } else {
            System.out.println("Student was not enrolled.");
        }
    }
}