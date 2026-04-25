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
    public static Database getInstance() {
        if (instance == null) {
            instance = loadFromFile();
            if (instance == null) instance = new Database();
        }
        return instance;
    }
    public void addUser(User u) {
         users.put(u.getLogin(), u);
         }
    public User getUserByLogin(String login){
         return users.get(login);
         }
    public Collection<User> getAllUsers() {
         return users.values();
         }

    public User authenticate(String login, String password) {
        User u = users.get(login);
        if (u != null && u.getPassword().equals(password)) return u;
        return null;
    }
    public void addCourse(Course c){
         courses.add(c);
         }
    public List<Course> getCourses(){
         return courses; 

    }
    public Course getCourseById(String id) {
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }
    public void addPaper(ResearchPaper p) { 
        papers.add(p);
     }
    public List<ResearchPaper> getPapers(){
         return papers;
         }
    public void addProject(ResearchProject p) {
         projects.add(p);
         }
    public List<ResearchProject> getProjects() {
         return projects; 
        }

   
    public void log(String message) {
        logs.add("[" + new Date() + "] " + message);
    }
    public List<String> getLogs() { return Collections.unmodifiableList(logs); }

   
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

    public void seedDemoData() {
        if (!users.isEmpty()) return;

        Admin admin = new Admin("admin", "admin123", "System", "Admin");
        addUser(admin);

        Teacher t1 = new Teacher("arman", "pass123", "Arman", "Myrzakanurov");
        Teacher t2 = new Teacher("miras", "pass123", "Miras", "Asubay");
        addUser(t1); addUser(t2);

        Student s1 = new Student("saniya",   "pass123", "Saniya",   "Niyazkhan",  "SE-2101");
        Student s2 = new Student("edige",    "pass123", "Edige",    "Sayak",      "CS-2201");
        Student s3 = new Student("nurasyl",  "pass123", "Mustafaev","Nurasyl",    "SE-2102");
        Student s4 = new Student("orkenbek", "pass123", "Mustafa",  "Orkenbek",   "CS-2202");
        addUser(s1); addUser(s2); addUser(s3); addUser(s4);

        Manager mgr = new Manager("", "pass123", "Asel", "Askarova");
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