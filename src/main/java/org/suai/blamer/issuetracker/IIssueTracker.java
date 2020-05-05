package org.suai.blamer.issuetracker;


import java.util.List;

public interface IIssueTracker {
    void parse(int start, int end, List<Integer> checkedIssues) throws IssueTrackerException;

    String getApiUrl(boolean assignee, boolean contibutors);
}
