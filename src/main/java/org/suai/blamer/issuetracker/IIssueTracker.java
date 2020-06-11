package org.suai.blamer.issuetracker;


import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.git.GitException;
import org.suai.blamer.issuetracker.ticket.Ticket;

import java.util.List;
import java.util.Map;

public interface IIssueTracker {
    void getContributors() throws IssueTrackerException;

    void getTickets(int start, int end, List<Integer> checkedIssues) throws IssueTrackerException;

    List<Integer> getNumbers();

    void findAssignee(BlameInspector blameInspector) throws IssueTrackerException, GitException;

    void setAssignee() throws IssueTrackerException;

    Map<Ticket, ItemAssignee> getWhoAssignee();

}
