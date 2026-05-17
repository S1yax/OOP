package research;
import java.io.Serializable;
import users.User;
//Сания
public class ResearchPaper implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Format { PLAIN, BIBTEX }
    private String title;
    private int year;
    private int citations;
    private int pages;
    private User author;

    public ResearchPaper(String title, int year, int citations, User author) {
        this.title = title;
        this.year= year;
        this.citations = citations;
        this.pages = 10; 
        this.author = author;
    }

    public ResearchPaper(String title, int year, int citations, int pages, User author) {
        this(title, year, citations, author);
        this.pages = pages;
    }

    
    public String getCitation(Format f) {
        if (f == Format.BIBTEX) {
            return String.format(
                    "@article{%s%d,\n  author = {%s},\n  title  = {%s},\n  year   = {%d},\n  note   = {Cited by %d}\n}",
                    author.getLastName().toLowerCase(), year,
                    author.getFullName(), title, year, citations);
        }
        return String.format("%s. \"%s\". %d. (Cited %d times, %d pages)",
                author.getFullName(), title, year, citations, pages);
    }
    public String getTitle(){ 
        return title; 
    }
    public int getYear() 
    { return year; 

    }
    public int getCitations() {
         return citations; 
        }
    public void setCitations(int c) {
         this.citations = c;
         }
    public int getPages() {
         return pages; 
        }
    public User getAuthor() {
         return author; 
        }

    @Override
    public String toString() {
        return String.format("\"%s\" (%d) by %s — %d citations, %d pages",
                title, year, author.getFullName(), citations, pages);
    }
}
