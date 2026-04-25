package research;
import exceptions.LowHIndexException;
import exceptions.NotAResearcherException;
import users.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResearchProject implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int MIN_SUPERVISOR_H_INDEX = 3;

    private String name;
    private Researcher supervisor;
    private List<Researcher> members = new ArrayList<>();
    private List<ResearchPaper> projectPapers = new ArrayList<>();

    public ResearchProject(String name, Researcher supervisor) throws LowHIndexException {
        int h = supervisor.calculateHIndex();
        if (h < MIN_SUPERVISOR_H_INDEX) {
            throw new LowHIndexException(
                    "Supervisor h-index (" + h + ") is below the required minimum of " + MIN_SUPERVISOR_H_INDEX);
        }
        this.name       = name;
        this.supervisor = supervisor;
        this.members.add(supervisor);
    }

    public void addMember(User user) throws NotAResearcherException {
        if (!(user instanceof Researcher)) {
            throw new NotAResearcherException(user.getFullName() + " is not a researcher.");
        }
        members.add((Researcher) user);
    }

    public void addPaper(ResearchPaper p) { projectPapers.add(p); }

    public String getName(){
         return name; 
        }
    public Researcher getSupervisor()  { 
        return supervisor; 
    }
    public List<Researcher> getMembers() {
         return members; 
        }
    public List<ResearchPaper> getPapers()  {
         return projectPapers;
         }

    @Override
    public String toString() {
        return "Project: " + name + " | Supervisor h-index: " + supervisor.calculateHIndex()
                + " | Members: " + members.size() + " | Papers: " + projectPapers.size();
    }
}
