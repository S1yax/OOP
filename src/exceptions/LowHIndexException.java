package exceptions;

import java.io.Serializable;

public class LowHIndexException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;
    public LowHIndexException(String message) {
        super(message);
    }
}