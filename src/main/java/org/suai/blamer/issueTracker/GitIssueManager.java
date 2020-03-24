package org.suai.blamer.issueTracker;

import com.google.gson.Gson;
import org.suai.blamer.git.GitException;
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

    public void parse(int start, int end) throws IssueTrackerException{
        try{
            boolean entryFlag = false;
            String apiUrl = getApiUrl();
            boolean emptyFlag = false;
            int pageNum = 0;
            while (!emptyFlag) {
                HttpURLConnection httpcon = (HttpURLConnection) new URL(apiUrl + "?page=" + pageNum).openConnection();
                pageNum++;
                BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                String issue = in.readLine();
                if (issue.equals("[]")){
                    emptyFlag = true;
                    continue;
                }
                int fromindex = 0;
                int i = 0;
                while (i != -1) {
                    StringBuilder numberTicket = new StringBuilder();
                    i = issue.indexOf("\"number\":", fromindex);
                    fromindex = i + 1;
                    if (i != -1) {
                        while (issue.charAt(i) != ':') {
                            i++;
                        }
                        while (issue.charAt(i) != ',') {
                            if (issue.charAt(i) == ':') {
                                i++;
                                continue;
                            }
                            numberTicket.append(issue.charAt(i));
                            i++;
                        }
                        int curNum = Integer.parseInt(numberTicket.toString());
                        if (entryFlag){
                            if (curNum < start){
                                return;
                            }
                        }
                        if (curNum >= start & curNum <= end) {
                            numbers.add(Integer.parseInt(numberTicket.toString()));
                            entryFlag = true;
                            httpcon = (HttpURLConnection) new URL(apiUrl + '/' + numberTicket).openConnection();
                            in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                            String curIssue = in.readLine();
                            Gson gson = new Gson();
                            Ticket ticket = gson.fromJson(curIssue, Ticket.class);
                            ticketpack.add(ticket);
                        }
                    }
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


    public String isAttach(String body) throws IssueTrackerException {
        String fileURL = this.url + "/files/";
        if (!body.contains(fileURL)){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i = body.indexOf(fileURL);
        while (body.charAt(i) != ')'){
            stringBuilder.append(body.charAt(i));
            i++;
        }
        try {
            HttpURLConnection httpcon = (HttpURLConnection) new URL(stringBuilder.toString()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String attachLine = in.readLine();
            stringBuilder = new StringBuilder();
            while (attachLine != null){
                stringBuilder.append(attachLine +'\n');
                attachLine = in.readLine();
            }
        }catch (IOException ex){
            throw new IssueTrackerException(ex);
        }
        return stringBuilder.toString();
    }


    public String getStacktrace(String body, boolean screenOut){
        if (body == null){
            return null;
        }
        int i = body.indexOf("at ");
        if (i == -1){
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        while(true){
            if (i == -1){
                return null;
            }
            if (i > 2){
                if (body.charAt(i - 1) == '\t'){
                    break;
                }
                if (body.charAt(i - 1) == ' ' & body.charAt(i - 2) == ' '){
                    break;
                }
            }
            i = body.indexOf("at ", i + 1);
        }
        int curi = i;
        while(i != -1){
            if (i - curi > 15){
                break;
            }
            if (body.charAt(i - 1) >= 65 & body.charAt(i - 1) <= 90){
                break;
            }
            if (body.charAt(i - 1) >= 97 & body.charAt(i - 1) <= 122){
                break;
            }
            StringBuilder tmpStringBuilder = new StringBuilder();
            while(body.charAt(i) != '\n'){
                tmpStringBuilder.append(body.charAt(i));
                i++;
            }
            stringBuilder.append(tmpStringBuilder.toString());
            curi = i;
            i = body.indexOf("at ", i);
            if (i != -1){
                if(screenOut) {
                    stringBuilder.append('\n');
                }
                if(!screenOut){
                    stringBuilder.append("<br>");
                }
            }
        }
        return stringBuilder.toString();
    }


    public ArrayList<Ticket> getTicketpack() {
        return ticketpack;
    }

}
