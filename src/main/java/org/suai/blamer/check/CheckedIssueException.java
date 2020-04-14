package org.suai.blamer.check;

public class CheckedIssueException extends Exception{

    public CheckedIssueException() {
    }

    public CheckedIssueException(String message) {
        super(message);
    }

    public CheckedIssueException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckedIssueException(Throwable cause) {
        super(cause);
    }

    public CheckedIssueException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
