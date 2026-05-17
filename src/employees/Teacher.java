package employees;

import communication.Message;
import database.Course;
import database.Database;
import database.Lesson;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import research.ResearcherApplication;
import students.Student;
import users.Mark;
import users.User;
//Едиге
public class Teacher extends Employee {

    private static final long serialVersionUID = 1L;

    private TeacherTitle title;

    public Teacher(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
        this.title = TeacherTitle.LECTOR;
    }

    public TeacherTitle getTitle() { return title; }
    public void setTitle(TeacherTitle t) { this.title = t; }

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
            System.out.println("\n=== TEACHER MENU — " + getFullName() + " [" + title + "] ===");
            System.out.println("1.  My courses");
            System.out.println("2.  Students in course");
            System.out.println("3.  Assign grade");
            System.out.println("4.  All grades in course");
            System.out.println("5.  Student transcript");
            System.out.println("6.  Add lesson to course");
            System.out.println("7.  View course lessons");
            System.out.println("8.  Edit student grade");
            System.out.println("9.  Send message");
            System.out.println("10. Inbox");
            System.out.println("11. Apply for researcher status");
            System.out.println("0.  Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1":
                    listMyCourses();
                    break;
                case "2":
                    viewStudents(sc);
                    break;
                case "3":
                    setGrade(sc, db);
                    break;
                case "4":
                    viewAllGrades(sc);
                    break;
                case "5":
                    viewTranscript(sc, db);
                    break;
                case "6":
                    addLesson(sc, db);
                    break;
                case "7":
                    viewLessons(sc);
                    break;
                case "8":
                    editGrade(sc, db);
                    break;
                case "9":
                    sendMessage(sc, db, Message.Type.REGULAR);
                    break;
                case "10":
                    viewInbox(db);
                    break;
                case "11":
                    applyForResearcher(sc, db);
                    break;
                case "0":
                    running = false;
                    db.log("Teacher [" + getLogin() + "] logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void listMyCourses() {
        List<Course> mine = getMyCourses();
        if (mine.isEmpty()) System.out.println("No courses assigned.");
        else {
            System.out.println("\n--- MY COURSES ---");
            mine.forEach(c -> System.out.printf("  [%s] %s (%d cr.) | Students: %d%n",
                    c.getId(), c.getName(), c.getCredits(), c.getEnrolledStudents().size()));
        }
    }

    private void viewStudents(Scanner sc) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        List<Student> students = c.getEnrolledStudents();
        if (students.isEmpty()) System.out.println("No students enrolled.");
        else {
            System.out.println("\n--- STUDENTS: " + c.getName() + " ---");
            students.forEach(s -> {
                Mark m = c.getMark(s);
                String grade = m == null ? "No grade" : "Total: " + String.format("%.1f", m.getTotal()) + " (" + m.getLetterGrade() + ")";
                System.out.printf("  %-20s [%-10s] %s%n", s.getFullName(), s.getGroup(), grade);
            });
        }
    }

    private void setGrade(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;

        List<Student> students = c.getEnrolledStudents();
        if (students.isEmpty()) { System.out.println("No students enrolled."); return; }

        System.out.println("Students in course:");
        students.forEach(s -> System.out.println("  " + s.getLogin() + " — " + s.getFullName()));

        System.out.print("Student login: ");
        String login = sc.nextLine().trim();
        User u = db.getUserByLogin(login);
        if (!(u instanceof Student)) { 
            System.out.println("Student not found."); return; 
        }
        Student st = (Student) u;
        if (!c.isEnrolled(st)) {
             System.out.println("Student is not enrolled in this course."); return;
             }

        Mark m = new Mark();
        try {
            System.out.print("Attestation 1 (0-30): ");
            m.setAttestation1(Double.parseDouble(sc.nextLine().trim()));
            System.out.print("Attestation 2 (0-30): ");
            m.setAttestation2(Double.parseDouble(sc.nextLine().trim()));
            System.out.print("Final exam (0-40): ");
            m.setFinalExam(Double.parseDouble(sc.nextLine().trim()));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format."); return;
        }

        c.putMark(st, m);
        db.log("Teacher [" + getLogin() + "] graded " + login + " in " + c.getId() + ": " + m);
        System.out.println("✅ Grade saved: " + m);
    }

    private void viewAllGrades(Scanner sc) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        System.out.println("\n--- GRADES: " + c.getName() + " ---");
        if (c.getAllMarks().isEmpty()) {
            System.out.println("  No grades recorded yet.");
            return;
        }
        c.getAllMarks().forEach((s, m) ->
                System.out.printf("  %-25s %s%n", s.getFullName(), m));

        double avg = c.getAllMarks().values().stream()
                .mapToDouble(Mark::getTotal).average().orElse(0);
        System.out.printf("  Course average: %.2f%n", avg);
    }

    private void viewTranscript(Scanner sc, Database db) {
        System.out.print("Student login: ");
        String login = sc.nextLine().trim();
        User u = db.getUserByLogin(login);
        if (!(u instanceof Student)) { System.out.println("Student not found."); return; }
        Student st = (Student) u;
        System.out.println("\n=== TRANSCRIPT: " + st.getFullName() + " | " + st.getGroup() + " ===");
        List<Course> courses = db.getCourses().stream()
                .filter(c -> c.isEnrolled(st)).collect(Collectors.toList());
        if (courses.isEmpty()) { System.out.println("No courses found."); return; }
        for (Course c : courses) {
            Mark m = c.getMark(st);
            System.out.printf("  %-35s %s%n", c.getName(),
                    m == null ? "Not graded" : m.toString());
        }
        System.out.printf("  GPA: %.2f%n", st.getGPA(db));
    }

    private void addLesson(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        System.out.print("Lesson type (LECTURE/PRACTICE/LAB): ");
        String typeStr = sc.nextLine().trim().toUpperCase();
        Lesson.LessonType type;
        try {
            type = Lesson.LessonType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type. Use: LECTURE, PRACTICE, LAB");
            return;
        }
        System.out.print("Day of week (e.g. Monday): ");
        String day = sc.nextLine().trim();
        System.out.print("Time (e.g. 09:00): ");
        String time = sc.nextLine().trim();
        System.out.print("Room: ");
        String room = sc.nextLine().trim();

        c.addLesson(new Lesson(type, day, time, room));
        db.log("Teacher [" + getLogin() + "] added lesson to " + c.getId());
        System.out.println("✅ Lesson added.");
    }

    private void viewLessons(Scanner sc) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        System.out.println("\n--- LESSONS: " + c.getName() + " ---");
        if (c.getLessons().isEmpty()) System.out.println("  No lessons scheduled.");
        else c.getLessons().forEach(l -> System.out.println("  " + l));
    }

    private void editGrade(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;

        if (c.getAllMarks().isEmpty()) {
            System.out.println("No grades recorded for this course.");
            return;
        }

        System.out.println("Students with grades:");
        c.getAllMarks().forEach((s, m) ->
                System.out.printf("  %-20s %s%n", s.getLogin() + " (" + s.getFullName() + ")", m));

        System.out.print("Student login to edit: ");
        String login = sc.nextLine().trim();
        User u = db.getUserByLogin(login);
        if (!(u instanceof Student)) { System.out.println("Student not found."); return; }
        Student st = (Student) u;
        if (!c.isEnrolled(st)) { System.out.println("Student is not enrolled in this course."); return; }

        Mark m = c.getMark(st);
        if (m == null) { System.out.println("No grade recorded yet. Use option 3."); return; }

        System.out.println("Current grade: " + m);
        try {
            System.out.print("New Attestation 1 (Enter to keep " + m.getAttestation1() + "): ");
            String v1 = sc.nextLine().trim();
            if (!v1.isBlank()) m.setAttestation1(Double.parseDouble(v1));

            System.out.print("New Attestation 2 (Enter to keep " + m.getAttestation2() + "): ");
            String v2 = sc.nextLine().trim();
            if (!v2.isBlank()) m.setAttestation2(Double.parseDouble(v2));

            System.out.print("New final exam (Enter to keep " + m.getFinalExam() + "): ");
            String v3 = sc.nextLine().trim();
            if (!v3.isBlank()) m.setFinalExam(Double.parseDouble(v3));
        } catch (NumberFormatException e) {
            System.out.println("Invalid format."); return;
        }

        c.putMark(st, m);
        db.log("Teacher [" + getLogin() + "] edited grade for " + login + " in " + c.getId() + ": " + m);
        System.out.println("✅ Grade updated: " + m);
    }

    private void applyForResearcher(Scanner sc, Database db) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║       RESEARCHER STATUS APPLICATION          ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Approval will upgrade you to ResearchTeacher║");
        System.out.println("║  Requirements:                               ║");
        System.out.println("║  • At least " + ResearcherApplication.MIN_PAPERS + " published papers            ║");
        System.out.println("║  • Research interests described              ║");
        System.out.println("║  • Affiliation provided                      ║");
        System.out.println("║  • Project idea provided                     ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        ResearcherApplication existing = db.getApplicationByLogin(getLogin());
        if (existing != null) {
            System.out.println("Your latest application: " + existing);
            if (existing.getStatus() == ResearcherApplication.Status.PENDING) {
                System.out.println("⏳ Application is already under review.");
                return;
            }
            if (existing.getStatus() == ResearcherApplication.Status.APPROVED) {
                System.out.println("✅ Already approved. Waiting for account update.");
                return;
            }
            System.out.println("❌ Rejected: " + existing.getReviewerComment());
            System.out.print("Submit again? (yes/no): ");
            if (!sc.nextLine().trim().equalsIgnoreCase("yes")) return;
        }

        System.out.print("Research interests: ");
        String interests = sc.nextLine().trim();
        if (interests.isBlank()) { System.out.println("This field is required."); return; }

        System.out.print("Affiliation (department): ");
        String affiliation = sc.nextLine().trim();
        if (affiliation.isBlank()) { System.out.println("This field is required."); return; }

        System.out.print("Project idea: ");
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
        db.submitApplication(app);
        System.out.println("\n✅ Application submitted! Status: " + app.getStatus());
        if (!app.meetsMinimumCriteria()) {
            System.out.println("⚠️  Warning: minimum criteria not fully met.");
        }
    }

    protected void sendMessage(Scanner sc, Database db, Message.Type type) {
        System.out.print("Recipient login: ");
        String to = sc.nextLine().trim();
        if (db.getUserByLogin(to) == null) { System.out.println("User not found."); return; }
        System.out.print("Subject: ");
        String subject = sc.nextLine().trim();
        System.out.print("Message: ");
        String body = sc.nextLine().trim();
        db.sendMessage(new Message(getLogin(), getFullName(), to, subject, body, type));
        System.out.println("Message sent.");
    }

    protected void viewInbox(Database db) {
        List<Message> inbox = db.getMessagesFor(getLogin());
        if (inbox.isEmpty()) { System.out.println("Inbox is empty."); return; }
        inbox.forEach(m -> System.out.println("\n" + m));
    }

    protected Course selectMyCourse(Scanner sc) {
        List<Course> mine = getMyCourses();
        if (mine.isEmpty()) { System.out.println("You have no courses."); return null; }
        System.out.println("Your courses:");
        mine.forEach(c -> System.out.printf("  [%s] %s%n", c.getId(), c.getName()));
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