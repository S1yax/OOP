package labwork_1;
import java.util.Vector;
enum Gender { BOY, GIRL } 
	class Person {
	    private Gender gender;
	    public Person(Gender gender) { this.gender = gender; }
	    public Gender getGender() { return gender; }
	    public String toString() { return gender.toString(); }
	}

	public class DragonLaunch {
	    private Vector<Person> line = new Vector<>();

	    public void kidnap(Person p) { line.add(p); }

	    public boolean willDragonEatOrNot() {
	        int boysToMatch = 0;
	        for (Person p : line) {
	            if (p.getGender() == Gender.BOY) {
	                boysToMatch++;
	            } else {
	                if (boysToMatch > 0) {
	                    boysToMatch--; 
	                } else {
	                    return true; 
	                }
	            }
	        }
	        return boysToMatch > 0; 
	    }
	    public static void main(String[] args) {
	        DragonLaunch dl = new DragonLaunch();
	        dl.kidnap(new Person(Gender.BOY));
	        dl.kidnap(new Person(Gender.BOY));
	        dl.kidnap(new Person(Gender.GIRL));
	        dl.kidnap(new Person(Gender.GIRL));
	        if (dl.willDragonEatOrNot()) System.out.println("Dragon will have a lunch!");
	        else System.out.println("No one left for dragon.");
	    }
	}

