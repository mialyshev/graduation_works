package org.suai.blamer.output;

import org.suai.blamer.issuetracker.ticket.Ticket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class HTMLPage {
    Map<Ticket, String> ticketStringMap;
    String outputFileName = "output.html";

    public HTMLPage(Map<Ticket, String> whoIs){
        ticketStringMap = whoIs;
    }

    public HTMLPage(Map<Ticket, String> whoIs, String fileName){
        ticketStringMap = whoIs;
        outputFileName = fileName;
    }

    public void out() throws IOException{
        if (!outputFileName.contains(".html")){
            outputFileName += ".html";
        }
        FileWriter writer = new FileWriter("./" + outputFileName, false);
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
