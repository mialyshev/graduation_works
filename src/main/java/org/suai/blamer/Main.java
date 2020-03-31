package org.suai.blamer;

import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.issueTracker.GitIssueManager;
import org.suai.blamer.issueTracker.IssueTrackerException;
import org.suai.blamer.output.HTMLPage;
import org.suai.blamer.output.Screen;

import java.io.IOException;

import java.io.InputStream;
import java.util.Properties;

public class Main{
    public static void main(String[] args) throws Exception {

        int startTicketNum = -1;
        int endTicketNum = -1;
        String out = null;
        try {
            if(args[0] != null){
                startTicketNum = Integer.parseInt(args[0]);
            }
            if(args[1] != null ){
                endTicketNum = Integer.parseInt(args[1]);
            }
            if(args[2] != null){
                out = args[2];
            }
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        Properties properties = new Properties();
        try {
            InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);
        } catch (IOException e) {
            e.getMessage();
        }

        String url = properties.getProperty("url");
        String path = properties.getProperty("path");

        GitIssueManager gitIssueManager = new GitIssueManager(url);
        try {
            gitIssueManager.parse(startTicketNum, endTicketNum);
        }catch (IssueTrackerException ex){
            ex.printStackTrace();
        }


        BlameInspector blameInspector = new BlameInspector(path);
        blameInspector.loadFolderInfo(path, "");

        gitIssueManager.findAssignee(blameInspector);

        if (out.equals("screen")){
            Screen screen = new Screen(gitIssueManager.getWhoAssignee());
            screen.out();
        }
        if(out.equals("html")){
            HTMLPage htmlPage = new HTMLPage(gitIssueManager.getWhoAssignee());
            htmlPage.out();
        }
    }
}
