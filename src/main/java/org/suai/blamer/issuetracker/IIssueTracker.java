package org.suai.blamer.issuetracker;


import java.util.ArrayList;

public interface IIssueTracker {
    void parse(int start, int end, ArrayList<Integer> checkedIssues) throws IssueTrackerException;

    String getApiUrl(boolean assignee, boolean contibutors);
}
