package org.suai.blamer.issueTracker;


public interface IIssueTracker {
    void parse() throws IssueTrackerException;

    String getApiUrl();
}
