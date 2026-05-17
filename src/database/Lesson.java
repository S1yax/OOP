package database;

import java.io.Serializable;
//Сания
public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum LessonType { LECTURE, PRACTICE, LAB, SEMINAR }

    private String subject;
    private LessonType type;
    private String dayOfWeek;
    private String time;
    private String room;

    // Полный конструктор с указанием предмета 
    public Lesson(String subject, LessonType type, String dayOfWeek, String time, String room) {
        this.subject   = subject;
        this.type      = type;
        this.dayOfWeek = dayOfWeek;
        this.time      = time;
        this.room      = room;
    }

    // Краткий конструктор 
    public Lesson(LessonType type, String dayOfWeek, String time, String room) {
        this(type.name(), type, dayOfWeek, time, room);
    }

    public String getSubject(){ 
        return subject;
     }
    public LessonType getType()  {
         return type;
        }
    public String getDayOfWeek() {
     return dayOfWeek; 
    }
    public String getTime(){
         return time; 
        }
    public String getRoom(){
         return room; 
        }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s %s | Ауд: %s", type, subject, dayOfWeek, time, room);
    }
}
