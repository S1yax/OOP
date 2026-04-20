package database;

import employees.Teacher;
import students.Student;
import users.Mark;

import java.io.Serializable;
import java.util.*;

public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private int credits;
    private Teacher instructor;
    private List<Student> enrolledStudents = new ArrayList<>();
    private Map<Student, Mark> marks       = new HashMap<>();
    private List<Lesson> lessons           = new ArrayList<>();

    public Course(String id, String name, int credits, Teacher instructor) {
        this.id         = id;
        this.name       = name;
        this.credits    = credits;
        this.instructor = instructor;
    }

    public boolean enroll(Student s) {
        if (!enrolledStudents.contains(s)) { enrolledStudents.add(s); return true; }
        return false;
    }

    public boolean unenroll(Student s)    { return enrolledStudents.remove(s); }
    public boolean isEnrolled(Student s)  { return enrolledStudents.contains(s); }
    public List<Student> getEnrolledStudents() { return Collections.unmodifiableList(enrolledStudents); }

    public void putMark(Student s, Mark m)   { marks.put(s, m); }
    public Mark getMark(Student s)           { return marks.get(s); }
    public Map<Student, Mark> getAllMarks()   { return Collections.unmodifiableMap(marks); }

    public void addLesson(Lesson l)          { lessons.add(l); }
    public List<Lesson> getLessons()         { return Collections.unmodifiableList(lessons); }

    public String getId()              { return id; }
    public String getName()            { return name; }
    public int getCredits()            { return credits; }
    public Teacher getInstructor()     { return instructor; }
    public void setInstructor(Teacher t) { this.instructor = t; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%d cr) — Instructor: %s",
                id, name, credits, instructor.getFullName());
    }
}