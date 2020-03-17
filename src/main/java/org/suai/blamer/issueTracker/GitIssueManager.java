package org.suai.blamer.issueTracker;

import com.google.gson.Gson;
import org.suai.blamer.issueTracker.ticket.Ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GitIssueManager implements IIssueTracker{
    private ArrayList<Ticket> ticketpack;
    private ArrayList<String> сollaborators;
    private ArrayList<Integer> numbers;
    private String url;

    public GitIssueManager(String url) throws IOException {
        ticketpack = new ArrayList<>();
        сollaborators = new ArrayList<>();
        numbers = new ArrayList<>();
        this.url = url;
    }

    public void parse() throws IssueTrackerException{
        try{
            String apiUrl = getApiUrl();
            HttpURLConnection httpcon = (HttpURLConnection) new URL(apiUrl).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String issue = in.readLine();
            int fromindex = 0;
            int i = 0;
            while (i != -1){
                StringBuilder numberTicket = new StringBuilder();
                i = issue.indexOf("number", fromindex);
                fromindex = i + 1;
                if (i != -1) {
                    while (issue.charAt(i) != ':') {
                        i++;
                    }
                    while (issue.charAt(i) != ',') {
                        if (issue.charAt(i) == ':'){
                            i++;
                            continue;
                        }
                        numberTicket.append(issue.charAt(i));
                        i++;
                    }
                    numbers.add(Integer.parseInt(numberTicket.toString()));

                    httpcon = (HttpURLConnection) new URL(apiUrl + '/' + numberTicket).openConnection();
                    in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                    String curIssue = in.readLine();
                    Gson gson = new Gson();
                    Ticket ticket = gson.fromJson(curIssue, Ticket.class);
                    ticketpack.add(ticket);
                }
            }
        }catch (IOException ex){
            throw new IssueTrackerException(ex);
        }

    }


    public String getApiUrl(){
        StringBuilder curUrl = new StringBuilder();
        curUrl.append("https://api.github.com/repos/");
        int infoIndex = url.indexOf("github.com");
        int fromindex = infoIndex + 1;
        infoIndex = url.indexOf('/', fromindex) + 1;
        while (infoIndex != url.length()){
            curUrl.append(url.charAt(infoIndex));
            infoIndex++;
        }
        curUrl.append("/issues");
        return curUrl.toString();
    }


    public Ticket getTicket(int number) throws IssueTrackerException {
        Iterator<Ticket>iterator = ticketpack.iterator();
        while (iterator.hasNext()){
            Ticket curTicket = iterator.next();
            if(Integer.parseInt(curTicket.number) == number){
                return curTicket;
            }
        }
        throw new IssueTrackerException("You entered an invalid number");
    }

    public ArrayList<Integer> getNumbers(){
        return numbers;
    }

    public void outNumbers(){
        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    public boolean checkNumber(int num){
        Iterator<Integer>iterator = numbers.iterator();
        while (iterator.hasNext()){
            if (iterator.next().equals(num)){
                return true;
            }
        }
        return false;
    }
}
