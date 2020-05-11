package org.suai.blamer.output;

import org.suai.blamer.issuetracker.Pair;
import org.suai.blamer.issuetracker.ticket.Ticket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class HTMLPage {
    private Map<Ticket, Pair> ticketStringMap;
    private String outputFileName = "output.html";

    public HTMLPage(Map<Ticket, Pair> whoIs) {
        ticketStringMap = whoIs;
    }

    public HTMLPage(Map<Ticket, Pair> whoIs, String fileName) {
        ticketStringMap = whoIs;
        outputFileName = fileName;
    }

    public void out() throws IOException {
        if (!outputFileName.contains(".html")) {
            outputFileName += ".html";
        }
        FileWriter writer = new FileWriter("./" + outputFileName, false);
        writer.append("<html>");
        writer.append("<head>BlameInspector</head>");
        writer.append("<body>");
        for (Map.Entry<Ticket, Pair> pair : ticketStringMap.entrySet()) {
            writer.append("<h3>Ticket № " + pair.getKey().getNumber() + "</h3>");
            if (pair.getValue().getSourceName() == "-1") {
                writer.append("<p>This ticket cannot be processed because the file with the error described in the ticket was changed" + "</p><br>");
                continue;
            }
            if (pair.getValue().getisAuthor() == true) {
                writer.append("<p>Assignee to " + pair.getValue().getSourceName() + "</p><br>");
            } else {
                writer.append("<p>Assignee to " + pair.getValue().getSourceName() + "<br>" + "Such user was not found in collaborators and cannot be appointed responsible for this issue." + "</p><br>");
            }
        }
        writer.append("</body>");
        writer.append("</html>");
        writer.flush();
        writer.close();
    }
}
