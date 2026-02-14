package labwork_1;
import java.util.Vector;
enum Degree {
    BACHELOR, MASTER, PHD
}

public class student{ 
    private static int total_students = 0;
    private static final String UNIVERSITY = "KBTU";
    private final int id;
    private String name;
    private Degree degree;

    {
        total_students++;
        id = total_students;
    }

    public student(String name) {
        this(name, Degree.BACHELOR);
    }

    public student(String name, Degree degree) {
        this.name = name;
        this.degree = degree;
    }

    public void study() {
        System.out.println(name + " is studying.");
    }

    public void study(String subject) {
        System.out.println(name + " is studying " + subject + ".");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Degree getDegree() {
        return degree;
    }

    public static int getTotalStudents() {
        return total_students;
    }

    public String toString() {
        return "Student #" + id + " [" + name + ", " + degree + "] at " + UNIVERSITY;
    }
} 
