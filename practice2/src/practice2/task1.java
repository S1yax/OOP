package practice2;

public class task1 {
    private String name;
    private int id;
    private int YOS;

    public task1(String name, int id) {
        this.name = name;
        this.id = id;
        this.YOS = 1;
    }

    public void displayDetails() {
        System.out.println("Name - " + name);
        System.out.println("ID - " + id);
        System.out.println("Year of Study - " + YOS);
    }
    
    public void incrementYearOfStudy() {
        YOS++;
    }

    public static void main(String[] args) {
        task1 stud = new task1("Saniya", 1);
        
        stud.displayDetails();
        stud.incrementYearOfStudy();
        stud.displayDetails();
    }
}