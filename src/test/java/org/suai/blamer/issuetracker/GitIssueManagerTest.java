package org.suai.blamer.issuetracker;


import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.suai.blamer.issuetracker.ticket.Ticket;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;


public class GitIssueManagerTest {

    @Test
    public void isAttach() throws IOException, IssueTrackerException {
        String url1 = "https://api.github.com/repos/mialyshev/webservice/issues/13";//no mes, no att
        String url2 = "https://api.github.com/repos/mialyshev/webservice/issues/12";//mes yes, att no
        String url3 = "https://api.github.com/repos/mialyshev/webservice/issues/11";//mes no, att yes
        String url4 = "https://api.github.com/repos/mialyshev/webservice/issues/10";//mes yes, att yes

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(url1);
        stringArrayList.add(url2);
        stringArrayList.add(url3);
        stringArrayList.add(url4);

        GithubIssueManager gitIssueManager = new GithubIssueManager("https://github.com/mialyshev/webservice");
        ArrayList<String> response = new ArrayList<>();

        Iterator<String> iterator = stringArrayList.iterator();
        while (iterator.hasNext()){
            HttpURLConnection httpcon = (HttpURLConnection) new URL(iterator.next()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String curIssue = in.readLine();
            Gson gson = new Gson();
            Ticket ticket = gson.fromJson(curIssue, Ticket.class);
            response.add(gitIssueManager.getStacktrace(gitIssueManager.isAttach(ticket.getBody())));
        }

        Assert.assertTrue(response.get(0) == null);
        Assert.assertTrue(response.get(1) == null);
        Assert.assertTrue(response.get(2) != null);
        Assert.assertTrue(response.get(3) != null);
    }

    @Test
    public void getStacktrace() throws IOException, IssueTrackerException {
        String url1 = "https://api.github.com/repos/mialyshev/webservice/issues/13";//no mes, no att
        String url2 = "https://api.github.com/repos/mialyshev/webservice/issues/12";//mes yes, att no
        String url3 = "https://api.github.com/repos/mialyshev/webservice/issues/11";//mes no, att yes
        String url4 = "https://api.github.com/repos/mialyshev/webservice/issues/10";//mes yes, att yes

        ArrayList<String> stringArrayList = new ArrayList<>();
        stringArrayList.add(url1);
        stringArrayList.add(url2);
        stringArrayList.add(url3);
        stringArrayList.add(url4);

        GithubIssueManager gitIssueManager = new GithubIssueManager("https://github.com/mialyshev/webservice");
        ArrayList<String> response = new ArrayList<>();

        Iterator<String> iterator = stringArrayList.iterator();
        while (iterator.hasNext()){
            HttpURLConnection httpcon = (HttpURLConnection) new URL(iterator.next()).openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
            String curIssue = in.readLine();
            Gson gson = new Gson();
            Ticket ticket = gson.fromJson(curIssue, Ticket.class);
            response.add(gitIssueManager.getStacktrace(ticket.getBody()));
        }

        Assert.assertTrue(response.get(0) == null);
        Assert.assertTrue(response.get(1) != null);
        Assert.assertTrue(response.get(2) == null);
        Assert.assertTrue(response.get(3) != null);
    }

}
