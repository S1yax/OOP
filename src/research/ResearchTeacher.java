package research;
import communication.Message;
import database.Course;
import database.Database;
import employees.Teacher;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import students.Student;
import users.Mark;
import users.User;
//Сания
public class ResearchTeacher extends Teacher implements Researcher {
    private static final long serialVersionUID = 1L;
    private List<ResearchPaper> papers = new ArrayList<>();
    public ResearchTeacher(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
    }
    // Researcher interface
    @Override
    public List<ResearchPaper> getPapers() { return papers; }

    @Override
    public void addPaper(ResearchPaper paper) { papers.add(paper); }

    // Menu
    @Override
    public void showMenu() {
        Scanner sc = new Scanner(System.in);
        Database db = Database.getInstance();
        boolean running = true;

        while (running) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║  RESEARCHER-TEACHER MENU — " + getFullName());
            System.out.println("║  h-index: " + calculateHIndex() + " | Publications: " + papers.size());
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  --- TEACHING ---                            ║");
            System.out.println("║  1.  My courses                              ║");
            System.out.println("║  2.  Students in course                      ║");
            System.out.println("║  3.  Assign grade                            ║");
            System.out.println("║  4.  All grades in course                    ║");
            System.out.println("║  5.  Student transcript                      ║");
            System.out.println("║  6.  Add lesson                              ║");
            System.out.println("║  7.  Edit grade                              ║");
            System.out.println("║  --- RESEARCH ---                            ║");
            System.out.println("║  8.  My publications                         ║");
            System.out.println("║  9.  Add publication                         ║");
            System.out.println("║  10. My h-index                              ║");
            System.out.println("║  11. Cite a publication                      ║");
            System.out.println("║  --- MENTORSHIP ---                          ║");
            System.out.println("║  12. My advisees (4th year)                  ║");
            System.out.println("║  13. Review researcher applications          ║");
            System.out.println("║  --- GENERAL ---                             ║");
            System.out.println("║  14. Send message                            ║");
            System.out.println("║  15. Inbox                                   ║");
            System.out.println("║  0.  Exit                                    ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Choice: ");

            switch (sc.nextLine().trim()) {
                case "1"  -> listMyCourses(db);
                case "2"  -> viewStudentsInCourse(sc, db);
                case "3"  -> setGradeMenu(sc, db);
                case "4"  -> viewAllGrades(sc, db);
                case "5"  -> viewTranscript(sc, db);
                case "6"  -> addLessonMenu(sc, db);
                case "7"  -> editGradeMenu(sc, db);
                case "8"  -> viewPapers(sc);
                case "9"  -> addPaperMenu(sc);
                case "10" -> System.out.println("Your h-index: " + calculateHIndex());
                case "11" -> showCitation(sc);
                case "12" -> viewMentees(db);
                case "13" -> reviewApplications(sc, db);
                case "14" -> sendMessage(sc, db, Message.Type.REGULAR);
                case "15" -> viewInbox(db);
                case "0"  -> {
                    running = false;
                    db.log("ResearchTeacher [" + getLogin() + "] logged out.");
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // Teaching methods
    private void listMyCourses(Database db) {
        List<Course> mine = getMyCourses();
        if (mine.isEmpty()) System.out.println("No courses assigned.");
        else mine.forEach(c -> System.out.printf("  [%s] %s (%d cr.) | Students: %d%n",
                c.getId(), c.getName(), c.getCredits(), c.getEnrolledStudents().size()));
    }

    private void viewStudentsInCourse(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        List<Student> students = c.getEnrolledStudents();
        if (students.isEmpty()) System.out.println("No students enrolled.");
        else students.forEach(s -> {
            Mark m = c.getMark(s);
            String grade = m == null ? "No grade" : "Total=" + String.format("%.1f", m.getTotal());
            System.out.printf("  %-20s [%s] %s%n", s.getFullName(), s.getGroup(), grade);
        });
    }

    private void setGradeMenu(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        List<Student> students = c.getEnrolledStudents();
        if (students.isEmpty()) { System.out.println("No students enrolled."); return; }
        students.forEach(s -> System.out.println("  " + s.getLogin() + " — " + s.getFullName()));
        System.out.print("Student login: ");
        String login = sc.nextLine().trim();
        User u = db.getUserByLogin(login);
        if (!(u instanceof Student)) { System.out.println("Student not found."); return; }
        Student st = (Student) u;
        if (!c.isEnrolled(st)) { System.out.println("Student is not enrolled in this course."); return; }
        Mark m = new Mark();
        try {
            System.out.print("Attestation 1 (0-30): ");
            m.setAttestation1(Double.parseDouble(sc.nextLine().trim()));
            System.out.print("Attestation 2 (0-30): ");
            m.setAttestation2(Double.parseDouble(sc.nextLine().trim()));
            System.out.print("Final exam (0-40): ");
            m.setFinalExam(Double.parseDouble(sc.nextLine().trim()));
        } catch (NumberFormatException e) { System.out.println("Invalid number format."); return; }
        c.putMark(st, m);
        db.log("ResearchTeacher [" + getLogin() + "] graded " + login + " in " + c.getId() + ": " + m);
        System.out.println("✅ Grade saved: " + m);
    }

    private void viewAllGrades(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        System.out.println("\n--- GRADES: " + c.getName() + " ---");
        if (c.getAllMarks().isEmpty()) { System.out.println("  No grades recorded."); return; }
        c.getAllMarks().forEach((s, m) -> System.out.printf("  %-25s %s%n", s.getFullName(), m));
        double avg = c.getAllMarks().values().stream().mapToDouble(Mark::getTotal).average().orElse(0);
        System.out.printf("  Course average: %.2f%n", avg);
    }

    private void viewTranscript(Scanner sc, Database db) {
        System.out.print("Student login: ");
        String login = sc.nextLine().trim();
        User u = db.getUserByLogin(login);
        if (!(u instanceof Student)) { System.out.println("Student not found."); return; }
        Student st = (Student) u;
        System.out.println("\n=== TRANSCRIPT: " + st.getFullName() + " ===");
        db.getCourses().stream().filter(c -> c.isEnrolled(st)).forEach(c -> {
            Mark m = c.getMark(st);
            System.out.printf("  %-35s %s%n", c.getName(), m == null ? "Not graded" : m.toString());
        });
        System.out.printf("  GPA: %.2f%n", st.getGPA(db));
    }

    private void addLessonMenu(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        System.out.print("Type (LECTURE/PRACTICE/LAB): ");
        String typeStr = sc.nextLine().trim().toUpperCase();
        database.Lesson.LessonType type;
        try {
            type = database.Lesson.LessonType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type."); return;
        }
        System.out.print("Day of week: ");
        String day = sc.nextLine().trim();
        System.out.print("Time: ");
        String time = sc.nextLine().trim();
        System.out.print("Room: ");
        String room = sc.nextLine().trim();
        c.addLesson(new database.Lesson(type, day, time, room));
        System.out.println("✅ Lesson added.");
    }

    private void editGradeMenu(Scanner sc, Database db) {
        Course c = selectMyCourse(sc);
        if (c == null) return;
        if (c.getAllMarks().isEmpty()) { System.out.println("No grades recorded."); return; }
        c.getAllMarks().forEach((s, m) -> System.out.printf("  %-20s %s%n",
                s.getLogin() + " (" + s.getFullName() + ")", m));
        System.out.print("Student login: ");
        String login = sc.nextLine().trim();
        User u = db.getUserByLogin(login);
        if (!(u instanceof Student)) { System.out.println("Student not found."); return; }
        Student st = (Student) u;
        Mark m = c.getMark(st);
        if (m == null) { System.out.println("No grade recorded for this student."); return; }
        System.out.println("Current grade: " + m);
        try {
            System.out.print("Att. 1 (Enter to keep " + m.getAttestation1() + "): ");
            String v1 = sc.nextLine().trim();
            if (!v1.isBlank()) m.setAttestation1(Double.parseDouble(v1));
            System.out.print("Att. 2 (Enter to keep " + m.getAttestation2() + "): ");
            String v2 = sc.nextLine().trim();
            if (!v2.isBlank()) m.setAttestation2(Double.parseDouble(v2));
            System.out.print("Final (Enter to keep " + m.getFinalExam() + "): ");
            String v3 = sc.nextLine().trim();
            if (!v3.isBlank()) m.setFinalExam(Double.parseDouble(v3));
        } catch (NumberFormatException e) { System.out.println("Invalid number format."); return; }
        c.putMark(st, m);
        System.out.println("✅ Grade updated: " + m);
    }

    // Research methods
    private void viewPapers(Scanner sc) {
        System.out.println("Sort by: 1=Citations  2=Year  3=Pages");
        String sort = sc.nextLine().trim();
        Comparator<ResearchPaper> cmp = switch (sort) {
            case "2" -> Researcher.byYear();
            case "3" -> Researcher.byPages();
            default  -> Researcher.byCitations();
        };
        printPapers(cmp);
    }

    private void addPaperMenu(Scanner sc) {
        System.out.print("Title: ");      String title = sc.nextLine().trim();
        int year, cites, pages;
        try {
            System.out.print("Year: ");year  = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Citations: ");cites = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Pages: ");pages = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format."); return;
        }
        addPaper(new ResearchPaper(title, year, cites, pages, this));
        System.out.println("✅ Publication added.");
    }

    private void showCitation(Scanner sc) {
        if (papers.isEmpty()) { 
            System.out.println("No publications found."); return;
         }
        papers.forEach(p -> System.out.println("  " + p));
        System.out.print("Enter publication title: ");
        String ptitle = sc.nextLine().trim();
        papers.stream().filter(p -> p.getTitle().equalsIgnoreCase(ptitle)).findFirst()
              .ifPresentOrElse(p -> {
                  System.out.println("\n____PLAIN____\n" + p.getCitation(ResearchPaper.Format.PLAIN));
                  System.out.println("\n___BIBTEX___\n" + p.getCitation(ResearchPaper.Format.BIBTEX));
              }, () -> System.out.println("Publication not found."));
    }

    // Mentorship
    private void viewMentees(Database db) {
        System.out.println("\n___MY ADVISEES (4th year students)___");
        boolean found = false;
        for (User u : db.getAllUsers()) {
            if (u instanceof Student) {
                Student st = (Student) u;
                if (st.getYearOfStudy() >= 4) {
                    String mentorLogin = db.getMentorLogin(st.getLogin());
                    if (getLogin().equals(mentorLogin)) {
                        System.out.printf("  %-20s | Group: %-10s | GPA: %.2f%n",
                                st.getFullName(), st.getGroup(), st.getGPA(db));
                        found = true;
                    }
                }
            }
        }
        if (!found) System.out.println("  No advisees assigned.");
    }

    // Review researcher applications
    private void reviewApplications(Scanner sc, Database db) {
        List<ResearcherApplication> pending = db.getPendingApplications();
        if (pending.isEmpty()) {
            System.out.println("No pending applications.");
            return;
        }
        System.out.println("\n____ RESEARCHER STATUS APPLICATIONS_____");
        for (int i = 0; i < pending.size(); i++) {
            ResearcherApplication app = pending.get(i);
            System.out.println("\n[" + (i+1) + "] " + app);
            System.out.println("Research interests : " + app.getResearchInterests());
            System.out.println("Affiliation        : " + app.getAffiliation());
            System.out.println("Project idea       : " + app.getProjectIdea());
            System.out.println("Papers claimed     : " + app.getExistingPapers());
            System.out.println("Meets min. criteria: " + (app.meetsMinimumCriteria() ? "✅ Yes" : "❌ No"));
            System.out.print("    Action (1=Approve / 2=Reject / Enter=Skip): ");
            String action = sc.nextLine().trim();
            if (action.equals("1")) {
                System.out.print("    Comment: ");
                app.approve(sc.nextLine().trim());
                db.log("ResearchTeacher [" + getLogin() + "] approved application: " + app.getApplicantLogin());
                System.out.println("✅ Application approved.");
            } else if (action.equals("2")) {
                System.out.print("    Reason for rejection: ");
                app.reject(sc.nextLine().trim());
                db.log("ResearchTeacher [" + getLogin() + "] rejected application: " + app.getApplicantLogin());
                System.out.println("❌ Application rejected.");
            }
        }
    }
}