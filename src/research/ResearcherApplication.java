package research;

import java.io.Serializable;
import java.util.Date;


 // Заявка пользователя на получение статуса ресёрчера. Критерии для одобрения (проверяются администратором/менеджером):минимум 2 опубликованные работы;h index >= 1;описание научных интересов заполнен;аффилиация (факультет/ организация) указана

 
public class ResearcherApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status { PENDING, APPROVED, REJECTED }

    //мин критерии
    public static final int MIN_PAPERS     = 2;
    public static final int MIN_H_INDEX    = 1;

    private String applicantLogin;
    private String applicantName;
    private String researchInterests;   // описание научных интересов
    private String affiliation;         // организация или факультет
    private String projectIdea;         // краткое описание идеи проекта
    private int    existingPapers;      // кол уже опубликованных работ
    private Status status;
    private String reviewerComment;
    private Date   submittedAt;
    private Date   reviewedAt;

    public ResearcherApplication(String applicantLogin, String applicantName,
                                 String researchInterests, String affiliation,
                                 String projectIdea, int existingPapers) {
        this.applicantLogin    = applicantLogin;
        this.applicantName     = applicantName;
        this.researchInterests = researchInterests;
        this.affiliation       = affiliation;
        this.projectIdea       = projectIdea;
        this.existingPapers    = existingPapers;
        this.status            = Status.PENDING;
        this.submittedAt       = new Date();
    }

    // ── Геттеры ────────────────────────────────────────────
    public String getApplicantLogin()    { return applicantLogin; }
    public String getApplicantName()     { return applicantName; }
    public String getResearchInterests() { return researchInterests; }
    public String getAffiliation()       { return affiliation; }
    public String getProjectIdea()       { return projectIdea; }
    public int    getExistingPapers()    { return existingPapers; }
    public Status getStatus()            { return status; }
    public String getReviewerComment()   { return reviewerComment; }
    public Date   getSubmittedAt()       { return submittedAt; }
    public Date   getReviewedAt()        { return reviewedAt; }

    // ── Действия ───────────────────────────────────────────
    public void approve(String comment) {
        this.status          = Status.APPROVED;
        this.reviewerComment = comment;
        this.reviewedAt      = new Date();
    }

    public void reject(String comment) {
        this.status          = Status.REJECTED;
        this.reviewerComment = comment;
        this.reviewedAt      = new Date();
    }

    // Проверка на соот мин критериям
    public boolean meetsMinimumCriteria() {
        return existingPapers >= MIN_PAPERS
            && !researchInterests.isBlank()
            && !affiliation.isBlank()
            && !projectIdea.isBlank();
    }

    @Override
    public String toString() {
        return String.format(
            "Заявка [%s] от %s | Работ: %d | Статус: %s | Подана: %s",
            applicantLogin, applicantName, existingPapers, status, submittedAt);
    }
}
