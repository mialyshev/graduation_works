package org.suai.blamer.issuetracker;

public class ItemTicketInfo {
    private Integer stringNumber;
    private Integer ticketNumber;

    public ItemTicketInfo(int stringNumber, int ticketNumber){
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
