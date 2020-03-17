package org.suai.blamer.issueTracker;

public class IssueTrackerException extends Exception {
    public IssueTrackerException() {
    }

    public IssueTrackerException(String message) {
        super(message);
    }

    public IssueTrackerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IssueTrackerException(Throwable cause) {
        super(cause);
    }

    public IssueTrackerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
