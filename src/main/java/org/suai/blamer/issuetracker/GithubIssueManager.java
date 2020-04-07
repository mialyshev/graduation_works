package org.suai.blamer.issuetracker;

import com.google.gson.Gson;
import org.suai.blamer.StackTrace;
import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.git.GitException;
import org.suai.blamer.issuetracker.ticket.Ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class GithubIssueManager implements IIssueTracker{

    private ArrayList<Ticket> ticketpack;
    private ArrayList<String> сollaborators;
    private ArrayList<Integer> numbers;
    private String url;
    private Map<Ticket, String>whoAssignee;
    final String apiGit = "https://api.github.com/repos/";
    final String github = "github.com";
    final String issues = "/issues";
    final String files = "/files/";


    public GithubIssueManager(String url){
        ticketpack = new ArrayList<>();
        сollaborators = new ArrayList<>();
        numbers = new ArrayList<>();
        this.url = url;
        whoAssignee = new HashMap<>();
    }

    public void parse(int start, int end) throws IssueTrackerException{
        try{
            boolean entryFlag = false;
            String apiUrl = getApiUrl();
            boolean emptyFlag = false;
            int pageNum = 1;
            while (!emptyFlag) {
                HttpURLConnection httpcon = (HttpURLConnection) new URL(apiUrl + "?page=" + pageNum).openConnection();
                pageNum++;
                BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                String issue = in.readLine();
                if (issue.equals("[]")){
                    emptyFlag = true;
                    continue;
                }
                int i = issue.indexOf("\"number\":", 0);
                while (i != -1) {
                    StringBuilder numberTicket = new StringBuilder();
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
                    i = issue.indexOf("\"number\":", i + 1);
                }
            }
        }catch (IOException ex){
            throw new IssueTrackerException(ex);
        }
    }


    public String getApiUrl(){
        StringBuilder curUrl = new StringBuilder();
        curUrl.append(apiGit);
        int infoIndex = url.indexOf(github);
        int fromindex = infoIndex + 1;
        infoIndex = url.indexOf('/', fromindex) + 1;
        while (infoIndex != url.length()){
            curUrl.append(url.charAt(infoIndex));
            infoIndex++;
        }
        curUrl.append(issues);
        return curUrl.toString();
    }


    public Ticket getTicket(int number) throws IssueTrackerException {
        Iterator<Ticket>iterator = ticketpack.iterator();
        while (iterator.hasNext()){
            Ticket curTicket = iterator.next();
            if(Integer.parseInt(curTicket.getNumber()) == number){
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
        String fileURL = this.url + files;
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


    public String getStacktrace(String body){
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
            if (body.charAt(i - 1) >= 'A' & body.charAt(i - 1) <= 'Z'){
                break;
            }
            if (body.charAt(i - 1) >= 'a' & body.charAt(i - 1) <= 'z'){
                break;
            }
            StringBuilder tmpStringBuilder = new StringBuilder();
            while(body.charAt(i) != '\n'){
                tmpStringBuilder.append(body.charAt(i));
                i++;
            }
            stringBuilder.append(tmpStringBuilder.toString() + '\n');
            curi = i;
            i = body.indexOf("at ", i);
        }
        return stringBuilder.toString();
    }

    public void findAssignee(BlameInspector blameInspector) throws IssueTrackerException, GitException{
        Iterator<Ticket>ticketIterator = ticketpack.iterator();
        try {
            while (ticketIterator.hasNext()) {
                Ticket ticket = ticketIterator.next();
                String bodyStack = getStacktrace(ticket.getBody());
                String attachStack = getStacktrace(isAttach(ticket.getBody()));
                StackTrace stackTrace;
                if (bodyStack != null) {
                    stackTrace = new StackTrace(blameInspector.getPath());
                    stackTrace.getLines(bodyStack, blameInspector.getFileInfo());
                    if (stackTrace.getFrame(0) != null) {
                        String fileName = stackTrace.getFrame(0).getFileName();
                        int numString = stackTrace.getFrame(0).getNumString();
                        String whoIs = blameInspector.blame(fileName, numString);
                        whoAssignee.put(ticket, whoIs);
                    }

                }
                if (attachStack != null) {
                    stackTrace = new StackTrace(blameInspector.getPath());
                    stackTrace.getLines(attachStack, blameInspector.getFileInfo());
                    if (stackTrace.getFrame(0) != null) {
                        String fileName = stackTrace.getFrame(0).getFileName();
                        int numString = stackTrace.getFrame(0).getNumString();
                        String whoIs = blameInspector.blame(fileName, numString);
                        if (!whoAssignee.isEmpty()) {
                            if (whoAssignee.get(ticket) != whoIs) {
                                whoAssignee.put(ticket, whoIs);
                            }
                        }
                    }
                }
            }
        }catch (IOException ex) {
            throw new IssueTrackerException(ex);
        }catch (GitException ex){
            throw new GitException(ex);
        }
    }


    public ArrayList<Ticket> getTicketpack() {
        return ticketpack;
    }


    public Map<Ticket, String> getWhoAssignee() {
        return whoAssignee;
    }


}
