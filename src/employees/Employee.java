package employees;
import users.User;
public abstract class Employee extends User {

    private static final long serialVersionUID = 1L;

    private String department;
    private double salary;

    public Employee(String login, String password, String firstName, String lastName) {
        super(login, password, firstName, lastName);
        this.department = "General";
        this.salary     = 0.0;
    }

    public String getDepartment() { 
        return department; 
    }
    public void setDepartment(String d) { 
        this.department = d; 
    }
    public double getSalary(){
         return salary;
        }
    public void setSalary(double s) {
         this.salary = s;
         }
}