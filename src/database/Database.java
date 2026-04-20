package database;

import employees.Manager;
import employees.Teacher;
import research.ResearchPaper;
import research.ResearchProject;
import students.Student;
import users.Admin;
import users.User;

import java.io.*;
import java.util.*;

/**
 * Singleton — one central data store for the entire system.
 */
public class Database implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE      = "university_data.ser";
    private static Database instance;

    private Map<String, User> users          = new HashMap<>();
    private List<Course> courses             = new ArrayList<>();
    private List<ResearchPaper> papers       = new ArrayList<>();
    private List<ResearchProject> projects   = new ArrayList<>();
    private List<String> logs                = new ArrayList<>();

    private Database() {}

    // ── Singleton ──────────────────────────────────────────
    public static Database getInstance() {
        if (instance == null) {
            instance = loadFromFile();
            if (instance == null) instance = new Database();
        }
        return instance;
    }

    // ── Users ──────────────────────────────────────────────
    public void addUser(User u)                   { users.put(u.getLogin(), u); }
    public User getUserByLogin(String login)       { return users.get(login); }
    public Collection<User> getAllUsers()          { return users.values(); }

    public User authenticate(String login, String password) {
        User u = users.get(login);
        if (u != null && u.getPassword().equals(password)) return u;
        return null;
    }

    // ── Courses ────────────────────────────────────────────
    public void addCourse(Course c)                { courses.add(c); }
    public List<Course> getCourses()               { return courses; }
    public Course getCourseById(String id) {
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    // ── Research ───────────────────────────────────────────
    public void addPaper(ResearchPaper p)          { papers.add(p); }
    public List<ResearchPaper> getPapers()         { return papers; }
    public void addProject(ResearchProject p)      { projects.add(p); }
    public List<ResearchProject> getProjects()     { return projects; }

    // ── Logs ───────────────────────────────────────────────
    public void log(String message) {
        logs.add("[" + new Date() + "] " + message);
    }
    public List<String> getLogs() { return Collections.unmodifiableList(logs); }

    // ── Persistence ────────────────────────────────────────
    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }

    private static Database loadFromFile() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Database) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    // ── Seed demo data ─────────────────────────────────────
    public void seedDemoData() {
        if (!users.isEmpty()) return;

        Admin admin = new Admin("admin", "admin123", "System", "Admin");
        addUser(admin);

        Teacher t1 = new Teacher("jsmith", "pass123", "John", "Smith");
        Teacher t2 = new Teacher("adoe",   "pass123", "Alice", "Doe");
        addUser(t1); addUser(t2);

        Student s1 = new Student("bob", "pass123", "Bob", "Brown", "SE-2101");
        Student s2 = new Student("eva", "pass123", "Eva", "Green", "CS-2201");
        addUser(s1); addUser(s2);

        Manager mgr = new Manager("carol", "pass123", "Carol", "White");
        addUser(mgr);

        Course oop  = new Course("CS101", "Object-Oriented Programming", 5, t1);
        Course algo = new Course("CS102", "Algorithms",                  4, t2);
        Course math = new Course("MA101", "Calculus",                    3, t1);
        addCourse(oop); addCourse(algo); addCourse(math);

        ResearchPaper p1 = new ResearchPaper("Deep Learning Survey", 2022, 120, t1);
        ResearchPaper p2 = new ResearchPaper("Graph Algorithms",     2023,  45, t2);
        addPaper(p1); addPaper(p2);

        System.out.println("Demo data seeded.");
    }
}