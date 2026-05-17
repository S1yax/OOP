package communication;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type {
         REGULAR, COMPLAINT
        }

    private String fromLogin;
    private String fromName;
    private String toLogin;
    private String subject;
    private String body;
    private Type type;
    private Date date;

    public Message(String fromLogin, String fromName, String toLogin,
                   String subject, String body, Type type) {
        this.fromLogin= fromLogin;
        this.fromName= fromName;
        this.toLogin= toLogin;
        this.subject= subject;
        this.body = body;
        this.type= type;
        this.date= new Date();
    }

    public String getFromLogin() {
         return fromLogin; 
        }
    public String getFromName()  {
         return fromName;
         }
    public String getToLogin(){
         return toLogin; 
        }
    public String getSubject(){
         return subject; 
        }
    public String getBody(){ 
        return body; 
    }
    public Type getType() { 
        return type;
     }
    public Date getDate() {
         return date;
         }

    @Override
    public String toString() {
        return String.format("[%tF] [%s] From: %s | To: %s\n  Subject: %s\n  %s",
                date, type, fromName, toLogin, subject, body);
    }
}
