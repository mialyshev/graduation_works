package org.suai.blamer.output;

import org.suai.blamer.issueTracker.GitIssueManager;
import org.suai.blamer.issueTracker.IssueTrackerException;
import org.suai.blamer.issueTracker.ticket.Ticket;

import java.util.ArrayList;
import java.util.Iterator;

public class Screen {

    ArrayList<Ticket> ticketArrayList;

    public Screen(ArrayList<Ticket> tickets){
        ticketArrayList = tickets;
    }

    public void out(GitIssueManager gitIssueManager) throws IssueTrackerException {
        Iterator<Ticket> iterator = ticketArrayList.iterator();

        while (iterator.hasNext()){
            Ticket ticket = iterator.next();
            System.out.println("Ticket №" + ticket.getNumber() + ":\nStackTrace on message:\n" + gitIssueManager.getStacktrace(ticket.getBody(), true)+ '\n');
            System.out.println("StackTrace on attach:\n" + gitIssueManager.getStacktrace(gitIssueManager.isAttach(ticket.getBody()), true) + '\n');
        }
    }
}
