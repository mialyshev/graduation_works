package org.suai.blamer.output;

import org.suai.blamer.issuetracker.ItemAssignee;
import org.suai.blamer.issuetracker.ticket.Ticket;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class HtmlOut {
    private Map<Ticket, ItemAssignee> ticketStringMap;
    private String outputFileName = "output.html";
    private Boolean isButtonReport = false;
    private String token;
    private final String ERROR = "<p>This ticket cannot be processed because the file with the error described in the ticket was changed</p><br>";
    private final String NOTCOLLABORATOR = "<br>Such user was not found in collaborators and cannot be appointed responsible for this issue.</p><br>";

    public HtmlOut(Map<Ticket, ItemAssignee> whoIs, String fileName, boolean isButton, String token) {
        ticketStringMap = whoIs;
        outputFileName = fileName;
        isButtonReport = isButton;
        this.token = token;
    }

    public HtmlOut(Map<Ticket, ItemAssignee> whoIs, boolean isButton, String token) {
        ticketStringMap = whoIs;
        isButtonReport = isButton;
        this.token = token;
    }

    public void writeReport() throws IOException {
        if (!outputFileName.contains(".html")) {
            outputFileName += ".html";
        }
        FileWriter writer = new FileWriter("./" + outputFileName, false);
        writer.append("<html>");
        writer.append("<head><h2>BlameInspector</h2><br>");
        writer.append(getScript());
        writer.append("</head>");
        writer.append("<body>");
        for (Map.Entry<Ticket, ItemAssignee> pair : ticketStringMap.entrySet()) {
            writer.append("<h3>Ticket № " + pair.getKey().getNumber() + "</h3>");
            if (pair.getValue().isDublicate()) {
                writer.append("<p>Ticket №" + pair.getKey().getNumber() + " is a duplicate of the Ticket №" + pair.getValue().getNumber() + "</p><br>");
                continue;
            }
            if (pair.getValue().getSourceName() == null) {
                writer.append(ERROR);
                continue;
            }
            if (pair.getValue().getisAuthor() == true) {
                writer.append("<p>Assignee to " + pair.getValue().getSourceName() + "</p><br>");
                writer.append("<button onclick=\"setassignee('" + pair.getKey().getUrl() + "', '" + token + "', '" + pair.getValue().getSourceName() + "')\">Assignee</button>");
            } else {
                writer.append("<p>Assignee to " + pair.getValue().getSourceName() + NOTCOLLABORATOR);
            }
        }
        writer.append("</body>");
        writer.append("</html>");
        writer.flush();
        writer.close();
    }

    private String getScript() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<script>\n");
        stringBuilder.append("function setassignee(url, token, name) {\n");
        stringBuilder.append("var xhr = new XMLHttpRequest();\n");
        stringBuilder.append("xhr.open(\"POST\", url, true);\n");
        stringBuilder.append("xhr.setRequestHeader('Authorization', 'token '+token);\n");
        stringBuilder.append("xhr.setRequestHeader('Content-Type', 'application/json');\n");
        stringBuilder.append("xhr.send(JSON.stringify({\n");
        stringBuilder.append("assignees: [name]\n");
        stringBuilder.append("}));\n");
        stringBuilder.append("}\n");
        stringBuilder.append("</script>\n");
        return stringBuilder.toString();
    }
}
