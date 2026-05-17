package employees;

import communication.Message;
import database.Course;
import database.Database;
import database.Lesson;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import research.ResearchTeacher;
import research.ResearcherApplication;
import students.Student;
import users.Mark;
import users.User;
//Орынбек
public class Manager extends Employee {

    private static final long serialVersionUID = 1L;

    private ManagerType managerType;

    public Manager(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
        this.managerType = ManagerType.OR;
    }

    public ManagerType getManagerType() {
         return managerType;
         }
    public void setManagerType(ManagerType t) {
         this.managerType = t;
         }

    @Override
    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        Database db = Database.getInstance();
        boolean running = true;

        while (running) {
            System.out.println("\n=== MANAGER MENU — " + getFullName() + " [" + managerType + "] ===");
            System.out.println("1.  All courses");
            System.out.println("2.  Add course");
            System.out.println("3.  Add lesson");
            System.out.println("4.  Enroll student in course");
            System.out.println("5.  Remove student from course");
            System.out.println("6.  Assign teacher");
            System.out.println("7.  All students (sorted)");
            System.out.println("8.  All teachers");
            System.out.println("9.  Academic performance report");
            System.out.println("10. Send message");
            System.out.println("11. Inbox");
            System.out.println("12. Researcher status applications");
            System.out.println("13. Assign mentor to 4th-year student");
            System.out.println("0.  Logout");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1"  -> db.getCourses().forEach(System.out::println);
                case "2"  -> addCourse(sc, db);
                case "3"  -> addLesson(sc, db);
                case "4"  -> enrollStudent(sc, db);
                case "5"  -> removeStudent(sc, db);
                case "6"  -> assignTeacher(sc, db);
                case "7"  -> viewStudents(sc, db);
                case "8"  -> viewTeachers(db);
                case "9"  -> academicReport(db);
                case "10" -> sendMessage(sc, db);
                case "11" -> viewInbox(db);
                case "12" -> reviewApplications(sc, db);
                case "13" -> assignMentor(sc, db);
                case "0"  -> {
                    running = false;
                    db.log("Manager [" + getLogin() + "] logged out.");
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private void addCourse(Scanner sc, Database db) {
        System.out.print("Course ID: ");        String id     = sc.nextLine().trim();
        System.out.print("Course name: ");      String name   = sc.nextLine().trim();
        System.out.print("Credits: ");
        int credits;
        try { credits = Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Invalid format."); return; }
        System.out.print("Teacher login: "); String tLogin = sc.nextLine().trim();
        User u = db.getUserByLogin(tLogin);
        if (!(u instanceof Teacher)) { System.out.println("Teacher not found."); return; }
        Course c = new Course(id, name, credits, (Teacher) u);
        db.addCourse(c);
        db.log("Manager added course: " + id);
        System.out.println("✅ Course added: " + c);
    }

    private void addLesson(Scanner sc, Database db) {
        System.out.print("Course ID: "); String cid = sc.nextLine().trim();
        Course c = db.getCourseById(cid);
        if (c == null) { System.out.println("Course not found."); return; }
        System.out.print("Type (LECTURE/PRACTICE/LAB): "); String type = sc.nextLine().trim().toUpperCase();
        Lesson.LessonType lt;
        try { lt = Lesson.LessonType.valueOf(type); }
        catch (IllegalArgumentException e) { System.out.println("Invalid type."); return; }
        System.out.print("Day of week: ");  String day  = sc.nextLine().trim();
        System.out.print("Time: ");         String time = sc.nextLine().trim();
        System.out.print("Room: ");         String room = sc.nextLine().trim();
        c.addLesson(new Lesson(lt, day, time, room));
        System.out.println("✅ Lesson added.");
    }

    private void enrollStudent(Scanner sc, Database db) {
        System.out.print("Student login: "); String sLogin = sc.nextLine().trim();
        System.out.print("Course ID: ");     String cid    = sc.nextLine().trim();
        User u = db.getUserByLogin(sLogin);
        Course c = db.getCourseById(cid);
        if (!(u instanceof Student) || c == null) { System.out.println("Student or course not found."); return; }
        Student s = (Student) u;
        int cur = s.getTotalCredits(db);
        if (cur + c.getCredits() > 21) {
            System.out.printf("Credit limit exceeded (%d + %d > 21).%n", cur, c.getCredits()); return;
        }
        if (c.enroll(s)) {
            db.log("Manager enrolled " + sLogin + " in " + cid);
            System.out.println("✅ Enrolled. Credits: " + (cur + c.getCredits()) + "/21");
        } else {
            System.out.println("Student is already enrolled.");
        }
    }

    private void removeStudent(Scanner sc, Database db) {
        System.out.print("Student login: "); String sLogin = sc.nextLine().trim();
        System.out.print("Course ID: ");     String cid    = sc.nextLine().trim();
        User u = db.getUserByLogin(sLogin);
        Course c = db.getCourseById(cid);
        if (!(u instanceof Student) || c == null) {
             System.out.println("Student or course not found."); return;
             }
        if (c.unenroll((Student) u)) {
            db.log("Manager removed " + sLogin + " from " + cid);
            System.out.println("✅ Student removed from course.");
        } else {
            System.out.println("Student was not enrolled.");
        }
    }

    private void assignTeacher(Scanner sc, Database db) {
        System.out.print("Course ID: "); String cid    = sc.nextLine().trim();
        System.out.print("Teacher login: ");  String tLogin = sc.nextLine().trim();
        Course c = db.getCourseById(cid);
        User u   = db.getUserByLogin(tLogin);
        if (c == null) { System.out.println("Course not found."); return; }
        if (!(u instanceof Teacher)) { System.out.println("Teacher not found."); return; }
        c.setInstructor((Teacher) u);
        db.log("Manager assigned " + tLogin + " to " + cid);
        System.out.println("✅ Assigned: " + u.getFullName() + " → " + c.getName());
    }

    private void viewStudents(Scanner sc, Database db) {
        System.out.println("Sort by: 1=GPA  2=Name  3=Group");
        String sort = sc.nextLine().trim();
        List<Student> students = db.getAllUsers().stream()
                .filter(u -> u instanceof Student).map(u -> (Student) u)
                .collect(Collectors.toList());
        if (students.isEmpty()) { System.out.println("No students found."); return; }
        switch (sort) {
            case "1" -> students.sort(Comparator.comparingDouble((Student s) -> s.getGPA(db)).reversed());
            case "3" -> students.sort(Comparator.comparing(Student::getGroup));
            default  -> students.sort(Comparator.comparing(User::getFullName));
        }
        students.forEach(s -> System.out.printf("  %-25s | Group: %-10s | Year: %d | GPA: %.2f%n",
                s.getFullName(), s.getGroup(), s.getYearOfStudy(), s.getGPA(db)));
    }

    private void viewTeachers(Database db) {
        db.getAllUsers().stream()
                .filter(u -> u instanceof Teacher)
                .forEach(u -> {
                    Teacher t = (Teacher) u;
                    String rt = (t instanceof ResearchTeacher) ? " [ResearchTeacher]" : "";
                    System.out.println("  " + t.getFullName() + " [" + t.getTitle() + "]" + rt);
                });
    }

    private void academicReport(Database db) {
        System.out.println("\n_____________ACADEMIC PERFORMANCE REPORT_____________");
        for (Course c : db.getCourses()) {
            System.out.println("\nCourse: " + c.getName() + " (" + c.getId() + ")");
            if (c.getAllMarks().isEmpty()) { System.out.println("  No grades recorded."); continue; }
            double sum = 0; int count = 0; int passed = 0;
            for (Mark m : c.getAllMarks().values()) {
                sum += m.getTotal(); count++;
                if (m.isPassed()) passed++;
            }
            System.out.printf("  Students graded : %d%n", count);
            System.out.printf("  Average score   : %.1f%n", sum / count);
            System.out.printf("  Pass rate       : %d/%d (%.0f%%)%n",
                    passed, count, (passed * 100.0 / count));
        }
        System.out.println("______________________________");
    }

    private void sendMessage(Scanner sc, Database db) {
        System.out.print("Recipient login: ");
        String to = sc.nextLine().trim();
        if (db.getUserByLogin(to) == null) { System.out.println("User not found."); return; }
        System.out.print("Subject: ");  String subject = sc.nextLine().trim();
        System.out.print("Message: ");  String body    = sc.nextLine().trim();
        db.sendMessage(new Message(getLogin(), getFullName(), to, subject, body, Message.Type.REGULAR));
        System.out.println("✅ Message sent.");
    }

    private void viewInbox(Database db) {
        List<Message> inbox = db.getMessagesFor(getLogin());
        if (inbox.isEmpty()) { System.out.println("Inbox is empty."); return; }
        inbox.forEach(m -> System.out.println("\n" + m));
    }

    private void reviewApplications(Scanner sc, Database db) {
        List<ResearcherApplication> pending = db.getPendingApplications();
        if (pending.isEmpty()) { System.out.println("No pending applications."); return; }
        System.out.println("\n--- RESEARCHER STATUS APPLICATIONS ---");
        for (ResearcherApplication app : pending) {
            System.out.println("\n" + app);
            System.out.println("  Interests  : " + app.getResearchInterests());
            System.out.println("  Affiliation: " + app.getAffiliation());
            System.out.println("  Project idea: " + app.getProjectIdea());
            System.out.println("  Papers     : " + app.getExistingPapers()
                    + " (required >= " + ResearcherApplication.MIN_PAPERS + ")");
            System.out.println("  Min. criteria: " + (app.meetsMinimumCriteria() ? "✅" : "❌"));
            System.out.print("  Action (1=Approve / 2=Reject / Enter=Skip): ");
            String action = sc.nextLine().trim();
            if (action.equals("1")) {
                System.out.print("  Comment: ");
                app.approve(sc.nextLine().trim());
                users.User existing = db.getUserByLogin(app.getApplicantLogin());
                if (existing != null && !(existing instanceof ResearchTeacher)) {
                    ResearchTeacher rt = new ResearchTeacher(
                            existing.getLogin(), existing.getPassword(),
                            existing.getFirstName(), existing.getLastName());
                    db.addUser(rt);
                    db.log("Manager upgraded [" + app.getApplicantLogin() + "] to ResearchTeacher");
                }
                System.out.println("✅ Approved. Role updated to ResearchTeacher.");
            } else if (action.equals("2")) {
                System.out.print("  Reason for rejection: ");
                app.reject(sc.nextLine().trim());
                System.out.println("❌ Rejected.");
            }
        }
    }

    private void assignMentor(Scanner sc, Database db) {
        System.out.println("\n--- ASSIGN MENTOR ---");
        List<Student> seniors = db.getAllUsers().stream()
                .filter(u -> u instanceof Student && ((Student) u).getYearOfStudy() >= 4)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
        if (seniors.isEmpty()) { System.out.println("No 4th-year students found."); return; }
        seniors.forEach(s -> {
            String m = db.getMentorLogin(s.getLogin());
            System.out.printf("  %-12s | %s | Mentor: %s%n",
                    s.getLogin(), s.getFullName(), m != null ? m : "—");
        });
        System.out.print("\nStudent login: ");
        String sLogin = sc.nextLine().trim();
        users.User su = db.getUserByLogin(sLogin);
        if (!(su instanceof Student) || ((Student) su).getYearOfStudy() < 4) {
            System.out.println("4th-year student not found."); return;
        }
        List<ResearchTeacher> rts = db.getResearchTeachers();
        if (rts.isEmpty()) { System.out.println("No ResearchTeachers in the system."); return; }
        rts.forEach(rt -> System.out.printf("  %-12s | %s | h-index: %d%n",
                rt.getLogin(), rt.getFullName(), rt.calculateHIndex()));
        System.out.print("Mentor login: ");
        String mLogin = sc.nextLine().trim();
        users.User mu = db.getUserByLogin(mLogin);
        if (!(mu instanceof ResearchTeacher)) {
            System.out.println("User is not a ResearchTeacher."); return;
        }
        db.setMentor(sLogin, mLogin);
        System.out.println("✅ Mentor assigned: " + mu.getFullName() + " → " + su.getFullName());
    }
}
