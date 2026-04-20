package patterns;

import research.Researcher;
import research.ResearchPaper;
import users.User;

import java.util.ArrayList;
import java.util.List;

public class ResearcherDecorator implements Researcher {

    private final User wrappedUser;
    private final List<ResearchPaper> papers = new ArrayList<>();

    public ResearcherDecorator(User user) {
        this.wrappedUser = user;
    }

    @Override
    public List<ResearchPaper> getPapers() { return papers; }

    @Override
    public void addPaper(ResearchPaper paper) { papers.add(paper); }

    public User getUser()         { return wrappedUser; }
    public String getFullName()   { return wrappedUser.getFullName(); }

    @Override
    public String toString() {
        return "[Researcher] " + wrappedUser.getFullName()
                + " | Papers: " + papers.size()
                + " | h-index: " + calculateHIndex();
    }
}