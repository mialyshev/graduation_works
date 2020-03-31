package org.suai.blamer.output;


import org.suai.blamer.issueTracker.ticket.Ticket;


import java.util.Map;

public class Screen {

    Map<Ticket, String> ticketStringMap;

    public Screen(Map<Ticket, String> whoIs){
        ticketStringMap = whoIs;
    }

    public void out(){
        for (Map.Entry<Ticket, String> pair : ticketStringMap.entrySet()) {
            System.out.println("Ticket №" + pair.getKey().getNumber() + "\nAssignee to " + pair.getValue());
        }
    }
}
