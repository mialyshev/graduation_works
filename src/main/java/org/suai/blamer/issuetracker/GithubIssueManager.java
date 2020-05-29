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
import org.suai.blamer.issuetracker.ticket.UserFullInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;


public class GithubIssueManager implements IIssueTracker {
    private static Logger logger = Logger.getLogger(GithubIssueManager.class.getName());
    private ArrayList<Ticket> ticketpack;
    private ArrayList<String> contibutors;
    private Map<String, String> contributorsFullInfo;
    private ArrayList<Integer> numbers;
    private String url;
    private Map<Ticket, Pair> whoAssignee;
    private String authString;
    private Map<String, TicketInfoPair> analyzedTicket;
    private static final String API_GIT = "https://api.github.com";
    private static final String REPOS = "/repos/";
    private static final String GITHUB = "github.com";
    private static final String ISSUES = "/issues";
    private static final String FILES = "/files/";
    private static final String ASSIGNEES = "/assignees";
    private static final String CONTRIBUTORS = "/contributors";
    private static final String USERS = "/users/";
    private static final String PAGE = "?page=";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "token ";


    public GithubIssueManager(String url, String token) {
        ticketpack = new ArrayList<>();
        contibutors = new ArrayList<>();
        numbers = new ArrayList<>();
        this.url = url;
        whoAssignee = new HashMap<>();
        authString = token;
        analyzedTicket = new HashMap<>();
        contributorsFullInfo = new HashMap<>();
    }

    private void setContibutors(User[] users) {
        for (int i = 0; i < users.length; i++){
            contibutors.add(users[i].login);
        }
    }

    private Integer setTickets(Ticket[] tickets, List<Integer> checkedIssued, int start, int end) {
        int num = 0;
        for (int i = 0; i < tickets.length; i++) {
            num = Integer.parseInt(tickets[i].getNumber());
            if (num < start) {
                break;
            }
            if (num >= start && num <= end && !checkedIssued.contains(num) && tickets[i].getAssignees().size() == 0) {
                ticketpack.add(tickets[i]);
                numbers.add(num);
            }
        }
        return num;
    }

    public void parse(int start, int end, List<Integer> checkedIssued) throws IssueTrackerException {
        logger.info("Start parsing");
        try {
            logger.info("Get contributors");
            int pageNum = 1;
            String contibutorURL = getApiUrl(false, true);
            HttpURLConnection httpcon = (HttpURLConnection) new URL(contibutorURL + PAGE + pageNum).openConnection();
            httpcon.setRequestProperty(AUTHORIZATION, TOKEN + authString);
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String curpage = in.readLine();
            Gson gson = new Gson();
            while (!curpage.equals("[]")) {
                User[] userArray = gson.fromJson(curpage, User[].class);
                setContibutors(userArray);
                pageNum++;
                httpcon = (HttpURLConnection) new URL(contibutorURL + PAGE + pageNum).openConnection();
                httpcon.setRequestProperty(AUTHORIZATION, TOKEN + authString);
                in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                curpage = in.readLine();
            }

            logger.info("Get contributors full info");
            Iterator<String> iterator = contibutors.iterator();
            while (iterator.hasNext()){
                contibutorURL = API_GIT + USERS + iterator.next();
                httpcon = (HttpURLConnection) new URL(contibutorURL).openConnection();
                httpcon.setRequestProperty(AUTHORIZATION, TOKEN + authString);
                in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                curpage = in.readLine();
                UserFullInfo userFullInfo = gson.fromJson(curpage, UserFullInfo.class);
                if (userFullInfo.name != null){
                    contributorsFullInfo.put(userFullInfo.name, userFullInfo.login);
                }
                if (userFullInfo.email != null){
                    contributorsFullInfo.put(userFullInfo.email, userFullInfo.login);
                }
            }

            logger.info("Start parsing issues (start :" + start + "; end : " + end + ")");
            String apiUrl = getApiUrl(false, false);
            pageNum = 1;
            logger.info("Send request to URL : " + apiUrl + PAGE + pageNum);
            httpcon = (HttpURLConnection) new URL(apiUrl + PAGE + pageNum).openConnection();
            httpcon.setRequestProperty(AUTHORIZATION, TOKEN + authString);
            in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String issue = in.readLine();
            while (!issue.equals("[]")) {
                Ticket[] tickets = gson.fromJson(issue, Ticket[].class);
                int lastticketnum = setTickets(tickets, checkedIssued, start, end);
                if (lastticketnum < start) {
                    break;
                }
                pageNum++;
                logger.info("Send request to URL : " + apiUrl + PAGE + pageNum);
                httpcon = (HttpURLConnection) new URL(apiUrl + PAGE + pageNum).openConnection();
                httpcon.setRequestProperty(AUTHORIZATION, TOKEN + authString);
                in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                issue = in.readLine();
            }

        } catch (IOException ex) {
            throw new IssueTrackerException(ex);
        }
    }

    public String getApiUrl(boolean assignee, boolean contributor) {
        StringBuilder curUrl = new StringBuilder();
        curUrl.append(API_GIT + REPOS);
        int infoIndex = url.indexOf(GITHUB);
        int fromindex = infoIndex + 1;
        infoIndex = url.indexOf('/', fromindex) + 1;
        while (infoIndex != url.length()) {
            curUrl.append(url.charAt(infoIndex));
            infoIndex++;
        }
        if (assignee) {
            curUrl.append(ASSIGNEES);
        } else if (contributor) {
            curUrl.append(CONTRIBUTORS);
        } else {
            curUrl.append(ISSUES);
        }
        return curUrl.toString();
    }


    public List<Integer> getNumbers() {
        return numbers;
    }

    public String isAttach(String body) throws IssueTrackerException {
        logger.info("Analysis of the body for the presence of a attach");
        String fileURL = this.url + FILES;
        if (body == null) {
            return null;
        }
        if (!body.contains(fileURL)) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        int i = body.indexOf(fileURL);
        while (body.charAt(i) != ')') {
            stringBuilder.append(body.charAt(i));
            i++;
        }
        try {
            HttpURLConnection httpcon = (HttpURLConnection) new URL(stringBuilder.toString()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String attachLine = in.readLine();
            stringBuilder = new StringBuilder();
            while (attachLine != null) {
                stringBuilder.append(attachLine + '\n');
                attachLine = in.readLine();
            }
        } catch (IOException ex) {
            throw new IssueTrackerException(ex);
        }
        return stringBuilder.toString();
    }


    private boolean isStackTrace(String body, int i) {
        if (i >= 2) {
            if (body.charAt(i - 1) >= 'A' && body.charAt(i - 1) <= 'Z') {
                return false;
            }
            if (body.charAt(i - 1) >= 'a' && body.charAt(i - 1) <= 'z') {
                i = body.indexOf("at ", i + 1);
                return false;
            }
            if (body.charAt(i - 1) == '\t') {
                return true;
            }
            if (body.charAt(i - 1) == '\n') {
                return true;
            }
            if (body.charAt(i - 1) == ' ' && body.charAt(i - 2) == ' ') {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getStacktrace(String body) {
        logger.info("Analyze body for find stacktrace");
        ArrayList<String> arrayList = new ArrayList<>();
        if (body == null) {
            return null;
        }
        int i = body.indexOf("at ");
        while (i != -1) {
            if (!isStackTrace(body, i)) {
                i = body.indexOf("at ", i + 1);
            } else {
                break;
            }
        }
        if (i == -1) {
            return null;
        }
        int next = body.indexOf("at ", i + 1);
        while (next != -1) {
            if (!isStackTrace(body, next)) {
                next = body.indexOf("at ", next + 1);
            } else {
                break;
            }
        }
        while (i != -1) {
            StringBuilder stringBuilder = new StringBuilder();
            while (body.charAt(i - 1) != ')' && i != body.length()) {
                if (next != -1 && i == next) {
                    break;
                }
                if (body.charAt(i) == '\r' || body.charAt(i) == '\n') {
                    while (true) {
                        if (i == body.length()) {
                            break;
                        }
                        if (body.charAt(i) == '\r' || body.charAt(i) == '\n' || body.charAt(i) == ' ') {
                            i++;
                        } else {
                            break;
                        }
                    }
                    continue;
                }
                stringBuilder.append(body.charAt(i));
                i++;
            }
            arrayList.add(stringBuilder.toString());
            i = next;
            if (i == -1) {
                break;
            }
            next = body.indexOf("at ", i + 1);
            while (next != -1) {
                if (!isStackTrace(body, next)) {
                    next = body.indexOf("at ", next + 1);
                } else {
                    break;
                }
            }
        }
        return arrayList;
    }


    public String getSourceName(String name){
        if (contributorsFullInfo.containsKey(name)){
            return contributorsFullInfo.get(name);
        }
        return null;
    }

    private Integer isDuplicate(String filename, int stringnum) {
        if (analyzedTicket.containsKey(filename)) {
            if (analyzedTicket.get(filename).getStringNumber() == stringnum) {
                return analyzedTicket.get(filename).getTicketNumber();
            }
        }
        return -1;
    }


    public void findAssignee(BlameInspector blameInspector) throws IssueTrackerException, GitException {
        Iterator<Ticket> ticketIterator = ticketpack.iterator();
        try {
            while (ticketIterator.hasNext()) {
                Ticket ticket = ticketIterator.next();
                logger.info("Start analyze ticket with number : " + ticket.getNumber() + " to search for a trace stack in it");
                ArrayList<String> bodyStack = getStacktrace(ticket.getBody());
                ArrayList<String> attachStack = getStacktrace(isAttach(ticket.getBody()));
                StackTrace stackTrace;
                if (bodyStack != null && !bodyStack.isEmpty()) {
                    logger.info("Start analyze stacktrace (body) in ticket with number : " + ticket.getNumber());
                    stackTrace = new StackTrace(blameInspector.getPath());
                    stackTrace.getLines(bodyStack, blameInspector.getFileInfo());
                    if (stackTrace.getFrame(0) != null) {
                        String fileName = stackTrace.getFrame(0).getFileName();
                        int numString = stackTrace.getFrame(0).getNumString();
                        int dublicate = isDuplicate(fileName, numString);
                        analyzedTicket.put(fileName, new TicketInfoPair(numString, Integer.parseInt(ticket.getNumber())));
                        if (dublicate == -1){
                            String whoIs = blameInspector.blame(fileName, numString);
                            if (whoIs == "-1") {
                                whoAssignee.put(ticket, new Pair(whoIs, false));
                            } else {
                                if (contibutors.contains(whoIs)) {
                                    whoAssignee.put(ticket, new Pair(whoIs, true));
                                } else {
                                    String sourcename = getSourceName(whoIs);
                                    if (sourcename != null){
                                        if (contibutors.contains(sourcename)) {
                                            whoAssignee.put(ticket, new Pair(sourcename, true));
                                        }
                                        else {
                                            whoAssignee.put(ticket, new Pair(whoIs, false));
                                        }
                                    }
                                    else {
                                        whoAssignee.put(ticket, new Pair(whoIs, false));
                                    }
                                }
                            }
                        }else{
                            whoAssignee.put(ticket, new Pair(true, dublicate));
                        }
                    }

                }
                if (attachStack != null && !attachStack.isEmpty()) {
                    logger.info("Start analyze stacktrace (attach) in ticket with number : " + ticket.getNumber());
                    stackTrace = new StackTrace(blameInspector.getPath());
                    stackTrace.getLines(attachStack, blameInspector.getFileInfo());
                    if (stackTrace.getFrame(0) != null) {
                        String fileName = stackTrace.getFrame(0).getFileName();
                        int numString = stackTrace.getFrame(0).getNumString();
                        int dublicate = isDuplicate(fileName, numString);
                        analyzedTicket.put(fileName, new TicketInfoPair(numString, Integer.parseInt(ticket.getNumber())));
                        if (dublicate == -1){
                            String whoIs = blameInspector.blame(fileName, numString);
                            if (whoIs == "-1") {
                                whoAssignee.put(ticket, new Pair(whoIs, false));
                            } else {
                                if (whoAssignee.containsKey(ticket)) {
                                    if (whoAssignee.get(ticket).getSourceName() != whoIs) {
                                        if (contibutors.contains(whoIs)) {
                                            whoAssignee.put(ticket, new Pair(whoIs, true));
                                        } else {
                                            String sourcename = getSourceName(whoIs);
                                            if (sourcename != null){
                                                if (contibutors.contains(sourcename)) {
                                                    whoAssignee.put(ticket, new Pair(sourcename, true));
                                                }
                                                else {
                                                    whoAssignee.put(ticket, new Pair(whoIs, false));
                                                }
                                            }else {
                                                whoAssignee.put(ticket, new Pair(whoIs, false));
                                            }
                                        }
                                    }
                                } else {
                                    if (contibutors.contains(whoIs)) {
                                        whoAssignee.put(ticket, new Pair(whoIs, true));
                                    } else {
                                        String sourcename = getSourceName(whoIs);
                                        if (sourcename != null){
                                            if (contibutors.contains(sourcename)) {
                                                whoAssignee.put(ticket, new Pair(sourcename, true));
                                            }
                                            else {
                                                whoAssignee.put(ticket, new Pair(whoIs, false));
                                            }
                                        }
                                        else {
                                            whoAssignee.put(ticket, new Pair(whoIs, false));
                                        }
                                    }
                                }
                            }
                        }else{
                            whoAssignee.put(ticket, new Pair(true, dublicate));
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new IssueTrackerException(ex);
        } catch (GitException ex) {
            throw new GitException(ex);
        }
    }


    public Map<Ticket, Pair> getWhoAssignee() {
        return whoAssignee;
    }


    public void setAssignee() throws IssueTrackerException {
        if (!whoAssignee.isEmpty()) {
            try {
                for (Map.Entry<Ticket, Pair> pair : whoAssignee.entrySet()) {
                    String curticketURL = pair.getKey().getUrl();
                    String user = pair.getValue().getSourceName();
                    if (user == "-1" || user == null) {
                        continue;
                    }
                    logger.info("Attempt to put assignee: " + user + " on ticket number : " + pair.getKey().getNumber());
                    String checkURL = getApiUrl(true, false);
                    HttpURLConnection httpcon = (HttpURLConnection) new URL(checkURL + "/" + user).openConnection();
                    httpcon.setRequestProperty(AUTHORIZATION, TOKEN + authString);
                    String status = httpcon.getHeaderField("Status");
                    if (status != null && status.contains("204")) {
                        HttpClient httpClient = HttpClientBuilder.create().build();
                        JSONObject json = new JSONObject();
                        ArrayList<String> list = new ArrayList<>();
                        list.add(user);
                        json.put("assignees", new JSONArray(list));
                        HttpPost request = new HttpPost(curticketURL);
                        request.addHeader(AUTHORIZATION, TOKEN + authString);
                        StringEntity params = new StringEntity(json.toString());
                        request.addHeader("content-type", "application/json");
                        request.setEntity(params);
                        httpClient.execute(request);
                    }
                }
            } catch (IOException e) {
                throw new IssueTrackerException(e);
            }
        }
    }

}
