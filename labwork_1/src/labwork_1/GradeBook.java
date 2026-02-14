package labwork_1;
import java.util.Scanner;
import java.util.Vector;

class Student {
    private String name;
    public Student(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
class Course {
    private String name, description, prerequisite;
    private int credits;

    public Course(String name, String description, int credits, String prerequisite) {
        this.name = name;
        this.description = description;
        this.credits = credits;
        this.prerequisite = prerequisite;
    }

    public String toString() {
        return name + " (" + description + ")";
    }
}
public class GradeBook {
    private Course course;
    private Vector<Student> students; 
    private Vector<Integer> grades;

    public GradeBook(Course course) {
        this.course = course;
        this.students = new Vector<>();
        this.grades = new Vector<>();
    }

    public void addStudent(Student s, int grade) { 
        students.add(s);
        grades.add(grade);
    }

    public void displayMessage() {
        System.out.println("Welcome to the grade book for " + course + "!");
    }

    public void displayGradeReport() {
        if (students.isEmpty()) {
            System.out.println("No students in the grade book.");
            return;
        }

        double avg = determineClassAverage();
        int minIdx = 0, maxIdx = 0;
        for (int i = 1; i < grades.size(); i++) {
            if (grades.get(i) < grades.get(minIdx)) minIdx = i;
            if (grades.get(i) > grades.get(maxIdx)) maxIdx = i;
        }

        System.out.printf("Class average is %.2f.\n", avg);
        System.out.println("Lowest grade is " + grades.get(minIdx) + " (Student " + students.get(minIdx).getName() + ").");
        System.out.println("Highest grade is " + grades.get(maxIdx) + " (Student " + students.get(maxIdx).getName() + ").");
        outputBarChart();
    }

    private double determineClassAverage() {
        if (grades.isEmpty()) return 0;
        double sum = 0;
        for (int g : grades) sum += g;
        return sum / grades.size();
    }

    private void outputBarChart() {
        System.out.println("Grades distribution:");
        int[] freq = new int[11];
        for (int g : grades) {
            if (g == 100) freq[10]++;
            else freq[g / 10]++;
        }

        for (int i = 0; i < freq.length; i++) {
            String label = (i == 10) ? "  100: " : String.format("%02d-%02d: ", i * 10, i * 10 + 9);
            System.out.print(label);
            for (int j = 0; j < freq[i]; j++) {
                System.out.print("*");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Course myCourse = new Course("CS101", "Object-oriented Programming", 5, "None");
        GradeBook myGradeBook = new GradeBook(myCourse);

        myGradeBook.displayMessage();
        myGradeBook.addStudent(new Student("Student A"), 85);
        myGradeBook.addStudent(new Student("Student B"), 92);
        myGradeBook.addStudent(new Student("Student C"), 45);
        myGradeBook.addStudent(new Student("Student D"), 100);

        myGradeBook.displayGradeReport();
    }
}