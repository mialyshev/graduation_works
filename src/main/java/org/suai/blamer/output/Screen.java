package org.suai.blamer.output;

import org.suai.blamer.issuetracker.Pair;
import org.suai.blamer.issuetracker.ticket.Ticket;

import java.util.Map;

public class Screen {
    private Map<Ticket, Pair> ticketStringMap;

    public Screen(Map<Ticket, Pair> whoIs) {
        ticketStringMap = whoIs;
    }

    public void out() {
        for (Map.Entry<Ticket, Pair> pair : ticketStringMap.entrySet()) {
            if (pair.getValue().getSourceName() == "-1") {
                System.out.println("Ticket №" + pair.getKey().getNumber() + "\nThis ticket cannot be processed because the file with the error described in the ticket was changed");
                continue;
            }
            if (pair.getValue().getisAuthor() == true) {
                System.out.println("Ticket №" + pair.getKey().getNumber() + "\nAssignee to " + pair.getValue().getSourceName());
            } else {
                System.out.println("Ticket №" + pair.getKey().getNumber() + "\nAssignee to " + pair.getValue().getSourceName() + ".\n" +
                        "Such user was not found in collaborators and cannot be appointed responsible for this issue.");
            }

        }
    }
}
