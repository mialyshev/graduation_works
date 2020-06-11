package org.suai.blamer.output;

import org.suai.blamer.issuetracker.ItemAssignee;
import org.suai.blamer.issuetracker.ticket.Ticket;

import java.util.Map;

public class Screen {
    private Map<Ticket, ItemAssignee> ticketStringMap;
    private final String ERROR = "\nThis ticket cannot be processed because the file with the error described in the ticket was changed";
    private final String NOTCOLLABORATOR = ".\nSuch user was not found in collaborators and cannot be appointed responsible for this issue.";

    public Screen(Map<Ticket, ItemAssignee> whoIs) {
        ticketStringMap = whoIs;
    }

    public void writeReport() {
        for (Map.Entry<Ticket, ItemAssignee> pair : ticketStringMap.entrySet()) {
            if (pair.getValue().isDublicate()) {
                System.out.println("Ticket №" + pair.getKey().getNumber() + " is a duplicate of the Ticket №" + pair.getValue().getNumber());
                continue;
            }
            if (pair.getValue().getSourceName() == null) {
                System.out.println("Ticket №" + pair.getKey().getNumber() + ERROR);
                continue;
            }
            if (pair.getValue().getisAuthor() == true) {
                System.out.println("Ticket №" + pair.getKey().getNumber() + "\nAssignee to " + pair.getValue().getSourceName());
            } else {
                System.out.println("Ticket №" + pair.getKey().getNumber() + "\nAssignee to " + pair.getValue().getSourceName() + NOTCOLLABORATOR);
            }

        }
    }
}
