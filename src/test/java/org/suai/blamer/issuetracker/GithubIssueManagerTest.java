package org.suai.blamer.issuetracker;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.suai.blamer.issuetracker.ticket.Ticket;
import org.suai.blamer.issuetracker.ticket.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GithubIssueManagerTest{

    @Test
    public void isAttach() throws IOException, IssueTrackerException {
        String url1 = "https://api.github.com/repos/mialyshev/testRepo/issues/1";//no att
        String url4 = "https://api.github.com/repos/mialyshev/testRepo/issues/2";//att yes

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(url1);
        stringArrayList.add(url4);

        GithubIssueManager gitIssueManager = new GithubIssueManager("https://github.com/mialyshev/testRepo", "token");
        ArrayList<String> response = new ArrayList<>();

        Iterator<String> iterator = stringArrayList.iterator();
        while (iterator.hasNext()) {
            HttpURLConnection httpcon = (HttpURLConnection) new URL(iterator.next()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String curIssue = in.readLine();
            Gson gson = new Gson();
            Ticket ticket = gson.fromJson(curIssue, Ticket.class);
            response.add(gitIssueManager.isAttach(ticket.getBody()));
        }

        Assert.assertTrue(response.get(0) == null);
        Assert.assertTrue(response.get(1) != null);
    }

    @Test
    public void getStacktrace() throws IOException, IssueTrackerException {
        String url1 = "https://api.github.com/repos/mialyshev/testRepo/issues/2";//mes no,  att yes
        String url2 = "https://api.github.com/repos/mialyshev/testRepo/issues/1";//mes yes, att no

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(url1);
        stringArrayList.add(url2);

        GithubIssueManager gitIssueManager = new GithubIssueManager("https://github.com/mialyshev/testRepo", "token");
        ArrayList<ArrayList<String>> response = new ArrayList<>();
        Iterator<String> iterator = stringArrayList.iterator();
        while (iterator.hasNext()) {
            HttpURLConnection httpcon = (HttpURLConnection) new URL(iterator.next()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String curIssue = in.readLine();
            Gson gson = new Gson();
            Ticket ticket = gson.fromJson(curIssue, Ticket.class);
            response.add(gitIssueManager.getStacktrace(ticket.getBody()));
            response.add(gitIssueManager.getStacktrace(gitIssueManager.isAttach(ticket.getBody())));
        }

        Assert.assertTrue(response.get(0) == null);
        Assert.assertTrue(response.get(1) != null);
        Assert.assertTrue(response.get(2) != null);
        Assert.assertTrue(response.get(3) == null);
    }

    @Test
    public void parse() throws IOException {
        String url1 = "https://api.github.com/repos/mialyshev/testRepo/issues/1";// ass yes
        String url2 = "https://api.github.com/repos/mialyshev/testRepo/issues/2";// ass no
        String url3 = "https://api.github.com/repos/mialyshev/testRepo/issues/3";// number

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(url1);
        stringArrayList.add(url2);
        stringArrayList.add(url3);
        int checkedNum = 3;

        GithubIssueManager gitIssueManager = new GithubIssueManager("https://github.com/mialyshev/testRepo", "token");
        ArrayList<List<User>> response = new ArrayList<>();
        ArrayList<Ticket> responseTicket = new ArrayList<>();
        Iterator<String> iterator = stringArrayList.iterator();
        while (iterator.hasNext()) {
            HttpURLConnection httpcon = (HttpURLConnection) new URL(iterator.next()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String curIssue = in.readLine();
            Gson gson = new Gson();
            Ticket ticket = gson.fromJson(curIssue, Ticket.class);
            response.add(ticket.getAssignees());
            if (Integer.parseInt(ticket.getNumber()) != checkedNum){
                responseTicket.add(ticket);
            }
        }

        Assert.assertTrue(!response.get(0).isEmpty());
        Assert.assertTrue(response.get(1).isEmpty());
        Assert.assertTrue(responseTicket.size() == 2);
    }
}