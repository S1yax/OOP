package patterns;

import employees.Manager;
import employees.Teacher;
import research.ResearchTeacher;
import students.Student;
import users.Admin;
import users.User;

public class UserFactory {

    private UserFactory() {}

    public static User createUser(String role, String login, String password,
                                  String firstName, String lastName) {
        switch (role.toLowerCase()) {
            case "student":    return new Student(login, password, firstName, lastName, "GEN-0000");
            case "teacher":    return new Teacher(login, password, firstName, lastName);
            case "researcher": return new ResearchTeacher(login, password, firstName, lastName);
            case "manager":    return new Manager(login, password, firstName, lastName);
            case "admin":      return new Admin(login, password, firstName, lastName);
            default: throw new IllegalArgumentException("Unknown role: " + role);
        }
    }
}