package org.suai.blamer.output;

import org.suai.blamer.issueTracker.GitIssueManager;
import org.suai.blamer.issueTracker.IssueTrackerException;
import org.suai.blamer.issueTracker.ticket.Ticket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class HTMLPage {
    Map<Ticket, String> ticketStringMap;

    public HTMLPage(Map<Ticket, String> whoIs){
        ticketStringMap = whoIs;
    }



    public void out() throws IOException, IssueTrackerException{
        FileWriter writer = new FileWriter("./output.html", false);
        writer.append("<html>");
        writer.append("<head>BlameInspector</head>");
        writer.append("<body>");
        for (Map.Entry<Ticket, String> pair : ticketStringMap.entrySet()) {
            writer.append("<h3>Ticket № " + pair.getKey().getNumber() + "</h3>");
            writer.append("<p>Assignee to " + pair.getValue() + "</p><br>");
        }
        writer.append("</body>");
        writer.append("</html>");
        writer.flush();
        writer.close();
    }
}
