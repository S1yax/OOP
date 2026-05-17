package communication;
import java.io.Serializable;
import java.util.Date;

//Simple news/notification system for the university. Also only admins can post news, but everyone can read them. Each news item has a title, content, author, and timestamp.
 
public class News implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String content;
    private String author;
    private Date date;

    public News(String title, String content, String author) {
        this.title   = title;
        this.content = content;
        this.author  = author;
        this.date    = new Date();
    }

	public String getTitle(){
         return title; 
    }
    public String getContent(){
         return content; }
    public String getAuthor() {
         return author; 
        }
    public Date getDate(){
         return date;
         }

    @Override
    public String toString() {
        return String.format("[%tF] %s (by %s)\n  %s", date, title, author, content);
    }
}

