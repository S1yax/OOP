package database;

import communication.Message;
import employees.Manager;
import employees.Teacher;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import research.ResearchPaper;
import research.ResearchProject;
import research.ResearchTeacher;
import research.ResearcherApplication;
import students.Student;
import users.Admin;
import users.User;
//Сания
public class Database implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String DATA_FILE      = "university_data.ser";
    private static Database instance;

    private Map<String, User>users= new HashMap<>();
    private List<Course>courses= new ArrayList<>();
    private List<ResearchPaper>papers = new ArrayList<>();
    private List<ResearchProject>projects= new ArrayList<>();
    private List<String>logs= new ArrayList<>();
    private List<Message> messages= new ArrayList<>();
    private List<ResearcherApplication> applications = new ArrayList<>();

    //логин студента/ логин его ResearchTeacher-наставника (для 4 курса)
    private Map<String, String> mentors= new HashMap<>();

    private Database() {}

    // синглтон для глобального доступа к данным
    public static Database getInstance() {
        if (instance == null) {
            instance = loadFromFile();
            if (instance == null) instance = new Database();
        }
        return instance;
    }

    //юзеры
    public void addUser(User u)  { 
        users.put(u.getLogin(), u); 
    }
    public void removeUser(String login)  {
         users.remove(login);
         }
    public User getUserByLogin(String login) {
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

    //курсы
    public void addCourse(Course c)    {
         courses.add(c); 
        }
    public List<Course> getCourses()   { 
        return courses;
     }
    public Course getCourseById(String id) {
        return courses.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    //сам ресерч
    public void addPaper(ResearchPaper p){ 
        papers.add(p); 
    }
    public List<ResearchPaper> getPapers()    {
         return papers;
         }
    public void addProject(ResearchProject p) {
         projects.add(p);
        }
    public List<ResearchProject> getProjects(){
        return projects;
     }

    // заявки на ресёрчера
    public void submitApplication(ResearcherApplication app) {
        applications.removeIf(a -> a.getApplicantLogin().equals(app.getApplicantLogin())
                               && a.getStatus() == ResearcherApplication.Status.PENDING);
        applications.add(app);
        log("The researcher application has been submitted.  " + app.getApplicantLogin());
    }

    public List<ResearcherApplication> getAllApplications() {
        return Collections.unmodifiableList(applications);
    }

    public List<ResearcherApplication> getPendingApplications() {
        return applications.stream()
                .filter(a -> a.getStatus() == ResearcherApplication.Status.PENDING)
                .collect(Collectors.toList());
    }

    public ResearcherApplication getApplicationByLogin(String login) {
        return applications.stream()
                .filter(a -> a.getApplicantLogin().equals(login))
                .max(Comparator.comparing(ResearcherApplication::getSubmittedAt))
                .orElse(null);
    }

    public boolean hasPendingApplication(String login) {
        return applications.stream()
                .anyMatch(a -> a.getApplicantLogin().equals(login)
                            && a.getStatus() == ResearcherApplication.Status.PENDING);
    }

    // Mentors (ResearchTeacher для студентов 4 курса)
    public void setMentor(String studentLogin, String mentorLogin) {
        mentors.put(studentLogin, mentorLogin);
        log("Mentor [" + mentorLogin + "] given to [" + studentLogin + "]");
    }

    public String getMentorLogin(String studentLogin) {
        return mentors.get(studentLogin);
    }

    public ResearchTeacher getMentor(String studentLogin) {
        String mLogin = mentors.get(studentLogin);
        if (mLogin == null) return null;
        User u = getUserByLogin(mLogin);
        return (u instanceof ResearchTeacher) ? (ResearchTeacher) u : null;
    }

    public List<ResearchTeacher> getResearchTeachers() {
        return users.values().stream()
                .filter(u -> u instanceof ResearchTeacher)
                .map(u -> (ResearchTeacher) u)
                .collect(Collectors.toList());
    }

    // сооб
    public void sendMessage(Message m)  { messages.add(m); }
    public List<Message> getMessagesFor(String login) {
        List<Message> inbox = new ArrayList<>();
        for (Message m : messages) {
            if (m.getToLogin().equals(login)) inbox.add(m);
        }
        return inbox;
    }
    public List<Message> getAllMessages() { return Collections.unmodifiableList(messages); }

    // логирование
    public void log(String message) { 
        logs.add("[" + new Date() + "] " + message); 
    }
    public List<String> getLogs()   { 
        return Collections.unmodifiableList(logs);
    }

    // Persistence
    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(this);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.err.println("ERROR " + e.getMessage());
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

    // демо данные для тестa
    public void seedDemoData() {
        if (!users.isEmpty()) return;

        Admin admin = new Admin("admin", "admin123", "System", "Admin");
        addUser(admin);

        ResearchTeacher rt = new ResearchTeacher("arman", "pass123", "Arman", "Myrzakanurov");
        rt.addPaper(new ResearchPaper("Deep Learning Survey",    2022, 120, 28, rt));
        rt.addPaper(new ResearchPaper("Neural Architecture NAS", 2023,  55, 18, rt));
        rt.addPaper(new ResearchPaper("Attention Mechanisms",    2021,  87, 22, rt));
        addUser(rt);

        Teacher t2 = new Teacher("miras", "pass123", "Miras", "Asubay");
        addUser(t2);

        Student s1 = new Student("saniya",   "pass123", "Saniya",  "Niyazkhan", "SE-2101");
        Student s2 = new Student("edige",    "pass123", "Edige",   "Sayak",     "CS-2201");
        Student s3 = new Student("nurasyl",  "pass123", "Nurasyl", "Mustafaev", "SE-2102");
        Student s4 = new Student("orkenbek", "pass123", "Orkenbek", "Mustafa",  "CS-2202");
        s1.setYearOfStudy(1);
        s2.setYearOfStudy(2);
        s3.setYearOfStudy(3);
        s4.setYearOfStudy(4);
        addUser(s1); addUser(s2); addUser(s3); addUser(s4);

        // Наставник для студента 4-го курса
        setMentor(s4.getLogin(), rt.getLogin());

        Manager mgr = new Manager("asel", "pass123", "Asel", "Askarova");
        addUser(mgr);

        Course oop  = new Course("CS101", "Object-Oriented Programming", 5, rt);
        Course algo = new Course("CS102", "Algorithms",                  4, t2);
        Course math = new Course("MA101", "Calculus",                    3, rt);
        addCourse(oop); addCourse(algo); addCourse(math);

        for (Course c : new Course[]{oop, algo, math}) {
            for (Student s : new Student[]{s1, s2, s3, s4}) {
                c.enroll(s);
            }
        }

        // Демо-оценки
        oop.putMark(s1,  new users.Mark(25, 27, 35));
        oop.putMark(s2,  new users.Mark(20, 22, 30));
        algo.putMark(s1, new users.Mark(28, 26, 38));
        algo.putMark(s3, new users.Mark(18, 20, 25));

        addPaper(new ResearchPaper("Graph Algorithms", 2023, 45, t2));

        System.out.println("Demo data seeded.");
    }
}
