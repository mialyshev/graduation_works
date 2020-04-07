package org.suai.blamer.issuetracker;


public interface IIssueTracker {
    void parse(int start, int end) throws IssueTrackerException;

    String getApiUrl();
}
