package org.suai.blamer.output;

import org.suai.blamer.issueTracker.GitIssueManager;
import org.suai.blamer.issueTracker.IssueTrackerException;
import org.suai.blamer.issueTracker.ticket.Ticket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class HTMLPage {
    ArrayList<Ticket> ticketArrayList;

    public HTMLPage(ArrayList<Ticket> tickets){
        ticketArrayList = tickets;
    }


    public void makePage(GitIssueManager gitIssueManager) throws IOException, IssueTrackerException {
        FileWriter writer = new FileWriter("./output.html", false);
        writer.append("<html>");
        writer.append("<head>BlameInspector</head>");
        writer.append("<body>");
        Iterator<Ticket>ticketIterator = ticketArrayList.iterator();
        while (ticketIterator.hasNext()){
            Ticket ticket = ticketIterator.next();
            writer.append("<h3>Ticket № " + ticket.getNumber() + "</h3>");
            writer.append("<p>StackTrace on message<br>" + gitIssueManager.getStacktrace(ticket.getBody(), false) + "</p>");
            writer.append("<p>StackTrace on attach:<br>" + gitIssueManager.getStacktrace(gitIssueManager.isAttach(ticket.getBody()), false) + "</p>");
        }
        writer.append("</body>");
        writer.append("</html>");
        writer.flush();
        writer.close();
    }
}
