package org.suai.blamer.issueTracker;


public interface IIssueTracker {
    void parse(int start, int end) throws IssueTrackerException;

    String getApiUrl();
}
