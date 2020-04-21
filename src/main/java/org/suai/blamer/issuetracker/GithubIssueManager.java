package org.suai.blamer.issuetracker;

import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.suai.blamer.StackTrace;
import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.git.GitException;
import org.suai.blamer.issuetracker.ticket.Ticket;
import org.suai.blamer.issuetracker.ticket.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

public class GithubIssueManager implements IIssueTracker{

    private static Logger logger = Logger.getLogger(GithubIssueManager.class.getName());
    private ArrayList<Ticket> ticketpack;
    private ArrayList<User> contibutors;
    private ArrayList<Integer> numbers;
    private String url;
    private Map<Ticket, String>whoAssignee;
    String authString;
    final String apiGit = "https://api.github.com/repos/";
    final String github = "github.com";
    final String issues = "/issues";
    final String files = "/files/";
    final String assignees = "/assignees";
    final String contibutorstr = "/contributors";


    public GithubIssueManager(String url, String token){
        ticketpack = new ArrayList<>();
        contibutors = new ArrayList<>();
        numbers = new ArrayList<>();
        this.url = url;
        whoAssignee = new HashMap<>();
        authString = token;
    }

    private void setContibutors(User[] users){
        for (int i = 0; i < users.length; i++){
            contibutors.add(users[i]);
        }
    }

    private Integer setTickets(Ticket[] tickets, ArrayList<Integer> checkedIssued, int start, int end){
        int num = 0;
        for (int i = 0; i < tickets.length; i++) {
            num = Integer.parseInt(tickets[i].getNumber());
            if (num >= start & num <= end & !checkedIssued.contains(num)){
                ticketpack.add(tickets[i]);
                numbers.add(num);
            }
        }
        return num;
    }

    public void parse(int start, int end, ArrayList<Integer> checkedIssued) throws IssueTrackerException{
        try{
            logger.info("Get contributors");
            int pageNum = 1;
            String contibutorURL = getApiUrl(false, true);
            HttpURLConnection httpcon = (HttpURLConnection) new URL(contibutorURL +"?page=" + pageNum).openConnection();
            httpcon.setRequestProperty("Authorization", "token " + authString);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String curpage = in.readLine();
            Gson gson = new Gson();
            while(!curpage.equals("[]")) {
                User[] userArray = gson.fromJson(curpage, User[].class);
                setContibutors(userArray);
                pageNum++;
                httpcon = (HttpURLConnection) new URL(contibutorURL +"?page=" + pageNum).openConnection();
                httpcon.setRequestProperty("Authorization", "token " + authString);
                in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                curpage = in.readLine();
            }

            logger.info("Start parsing issues (start :" + start + "; end : " + end + ")");
            String apiUrl = getApiUrl(false, false);
            pageNum = 1;
            logger.info("Send request to URL : " + apiUrl + "?page=" + pageNum);
            httpcon = (HttpURLConnection) new URL(apiUrl + "?page=" + pageNum).openConnection();
            httpcon.setRequestProperty("Authorization", "token " + authString);
            in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String issue = in.readLine();
            while (!issue.equals("[]")){
                Ticket[] tickets = gson.fromJson(issue, Ticket[].class);
                int lastticketnum = setTickets(tickets, checkedIssued, start, end);
                if(lastticketnum < start){
                    break;
                }
                pageNum++;
                logger.info("Send request to URL : " + apiUrl + "?page=" + pageNum);
                httpcon = (HttpURLConnection) new URL(apiUrl +"?page=" + pageNum).openConnection();
                httpcon.setRequestProperty("Authorization", "token " + authString);
                in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                issue = in.readLine();
            }

        }catch (IOException ex){
            throw new IssueTrackerException(ex);
        }
    }


    public String getApiUrl(boolean assignee, boolean contributor){
        StringBuilder curUrl = new StringBuilder();
        curUrl.append(apiGit);
        int infoIndex = url.indexOf(github);
        int fromindex = infoIndex + 1;
        infoIndex = url.indexOf('/', fromindex) + 1;
        while (infoIndex != url.length()){
            curUrl.append(url.charAt(infoIndex));
            infoIndex++;
        }
        if (assignee){
            curUrl.append(assignees);
        }else if(contributor){
            curUrl.append(contibutorstr);
        }else {
            curUrl.append(issues);
        }
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
        logger.info("Analysis of the body for the presence of a attach");
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
        logger.info("Analyze body for find stacktrace");
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
            while(body.charAt(i - 1) != ')'){
                if(body.charAt(i) == '\r' | body.charAt(i) == '\n'){
                    while(true){
                        if(body.charAt(i) == '\r' | body.charAt(i) == '\n' | body.charAt(i) == ' '){
                            i++;
                        }else {
                            break;
                        }
                    }
                }
                tmpStringBuilder.append(body.charAt(i));
                i++;
            }
            stringBuilder.append(tmpStringBuilder.toString() + '\n');
            curi = i;
            i = body.indexOf("at ", i);
        }
        return stringBuilder.toString();
    }


    private String getSourceName(String name){
        Iterator<User>iterator = contibutors.iterator();
        while (iterator.hasNext()){
            String curLogin = iterator.next().getLogin();
            if(curLogin.equals(name)){
                return curLogin;
            }
        }
        return null;
    }

    public void findAssignee(BlameInspector blameInspector) throws IssueTrackerException, GitException{
        Iterator<Ticket>ticketIterator = ticketpack.iterator();
        try {
            while (ticketIterator.hasNext()) {
                Ticket ticket = ticketIterator.next();
                logger.info("Start analyze ticket with number : " + ticket.getNumber() + " to search for a trace stack in it");
                String bodyStack = getStacktrace(ticket.getBody());
                String attachStack = getStacktrace(isAttach(ticket.getBody()));
                StackTrace stackTrace;
                if (bodyStack != null) {
                    logger.info("Start analyze stacktrace (body) in ticket with number : " + ticket.getNumber());
                    stackTrace = new StackTrace(blameInspector.getPath());
                    stackTrace.getLines(bodyStack, blameInspector.getFileInfo());
                    if (stackTrace.getFrame(0) != null) {
                        String fileName = stackTrace.getFrame(0).getFileName();
                        int numString = stackTrace.getFrame(0).getNumString();
                        String whoIs = blameInspector.blame(fileName, numString);
                        String sourcename = getSourceName(whoIs);
                        if (sourcename != null) {
                            whoAssignee.put(ticket, whoIs);
                        }
                    }

                }
                if (attachStack != null) {
                    logger.info("Start analyze stacktrace (attach) in ticket with number : " + ticket.getNumber());
                    stackTrace = new StackTrace(blameInspector.getPath());
                    stackTrace.getLines(attachStack, blameInspector.getFileInfo());
                    if (stackTrace.getFrame(0) != null) {
                        String fileName = stackTrace.getFrame(0).getFileName();
                        int numString = stackTrace.getFrame(0).getNumString();
                        String whoIs = blameInspector.blame(fileName, numString);
                        String sourcename = getSourceName(whoIs);
                        if (sourcename != null) {
                            if (whoAssignee.containsKey(ticket)) {
                                if (whoAssignee.get(ticket) != whoIs) {
                                    whoAssignee.put(ticket, whoIs);
                                }
                            } else {
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


    public void setAssignee() throws IssueTrackerException{
        if (!whoAssignee.isEmpty()) {
            try {
                for (Map.Entry<Ticket, String> pair : whoAssignee.entrySet()) {
                    String curticketURL = pair.getKey().getUrl();
                    String user = pair.getValue();
                    logger.info("Attempt to put assignee: " + user +  " on ticket number : " + pair.getKey().getNumber());
                    String checkURL = getApiUrl(true, false);
                    HttpURLConnection httpcon = (HttpURLConnection) new URL(checkURL + "/" + user).openConnection();
                    httpcon.setRequestProperty("Authorization", "token " + authString);
                    String status = httpcon.getHeaderField("Status");
                    if(status != null){
                        if (status.contains("204")) {
                            HttpClient httpClient = HttpClientBuilder.create().build();
                            JSONObject json = new JSONObject();
                            ArrayList<String> list = new ArrayList<String>();
                            list.add(user);
                            json.put("assignees", new JSONArray(list));
                            HttpPost request = new HttpPost(curticketURL);
                            request.addHeader("Authorization", "token " + authString);
                            StringEntity params = new StringEntity(json.toString());
                            request.addHeader("content-type", "application/json");
                            request.setEntity(params);
                            httpClient.execute(request);
                        }
                    }
                }
            } catch (MalformedURLException e) {
                throw new IssueTrackerException(e);
            } catch (IOException e) {
                throw new IssueTrackerException(e);
            }
        }
    }

}
