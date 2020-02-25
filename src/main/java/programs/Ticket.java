package programs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Ticket {
    Map<String, String> ticketinfo;
    Map<String, String> userinfo;
    Map<String, String> pull_requestinfo;
    Map<String, String> labelsinfo;
    Map<String, String> closedinfo;

    public Ticket(){
        ticketinfo = new HashMap<String, String>();
        userinfo = new HashMap<String, String>();
        pull_requestinfo = new HashMap<String, String>();
        labelsinfo = new HashMap<String, String>();
        closedinfo = new HashMap<String, String>();
    }

    public void addTicketInfo(String key, String value){
        if (!ticketinfo.containsKey(key)){
            ticketinfo.put(key, value);
        }
    }

    public void addUserInfo(String key, String value){
        if (!userinfo.containsKey(key)){
            userinfo.put(key, value);
        }
    }

    public void addPullRequestInfo(String key, String value){
        if (!pull_requestinfo.containsKey(key)){
            pull_requestinfo.put(key, value);
        }
    }

    public void addLabelsInfo(String key, String value){
        if (!labelsinfo.containsKey(key)){
            labelsinfo.put(key, value);
        }
    }

    public void addClosedInfo(String key, String value){
        if (!closedinfo.containsKey(key)){
            closedinfo.put(key, value);
        }
    }

    public void outTicketInfo() {
        System.out.println("\nGeneral information :");
        if (!ticketinfo.isEmpty()) {
            for (Map.Entry<String, String> entry : ticketinfo.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
        System.out.println("\nTicket Author Information :");
        if (!userinfo.isEmpty()) {
            for (Map.Entry<String, String> entry : userinfo.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }

        if (!pull_requestinfo.isEmpty()) {
            System.out.println("\nInformation about pull request :");
            for (Map.Entry<String, String> entry : pull_requestinfo.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }

        if (!labelsinfo.isEmpty()) {
            System.out.println("\nInformation about labels :");
            for (Map.Entry<String, String> entry : labelsinfo.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }

        if (closedinfo.size() > 2) {
            System.out.println("\nInformation about user, who closed this ticket :");
            for (Map.Entry<String, String> entry : closedinfo.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }else{
            System.out.println("\nThis ticket is not closed by anyone");
        }

    }

    public int getNumber(){
        return Integer.parseInt(ticketinfo.get("number"));
    }

}
