package org.suai.blamer.issuetracker;

public class TicketInfoPair {
    private Integer stringNumber;
    private Integer ticketNumber;

    public TicketInfoPair(int stringNumber, int ticketNumber){
        this.stringNumber = stringNumber;
        this.ticketNumber = ticketNumber;
    }

    public Integer getStringNumber() {
        return stringNumber;
    }

    public Integer getTicketNumber() {
        return ticketNumber;
    }
}
