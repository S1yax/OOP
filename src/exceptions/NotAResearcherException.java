package exceptions;

import java.io.Serializable;

public class NotAResearcherException extends Exception implements Serializable {

    private static final long serialVersionUID = 1L;

    public NotAResearcherException(String message) {
        super(message);
    }
}