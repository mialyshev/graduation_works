package org.suai.blamer;

import org.apache.commons.cli.*;
import org.suai.blamer.check.CheckedIssueAnalyzer;
import org.suai.blamer.check.CheckedIssueException;
import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.git.GitException;
import org.suai.blamer.issuetracker.GithubIssueManager;
import org.suai.blamer.issuetracker.IssueTrackerException;
import org.suai.blamer.output.HTMLPage;
import org.suai.blamer.output.Screen;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Main{
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addOption("s", "start", true, "Ticket start number. Default = 0");
        options.addOption("e", "end", true, "Ticket end number. Default = 100000");
        options.addOption("o", "out", true, "You can write either 'screen' or 'html'. Default = screen");
        options.addRequiredOption("l", "login", true, "Login from your github");
        options.addRequiredOption("p", "password", true, "Password from your github");
        options.addOption("f", "file", true, "The name of the file to display in html. Default = output.html");
        options.addOption("c", "config properties", true, "Path to file 'config.properties'");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            int startTicketNum = 0;
            int endTicketNum = 100000;
            String out = "screen";
            String htmlFilename = null;
            String login = null;
            String pwd = null;
            String configPath = "config.properties";

            if (cmd.hasOption("s")) {
                startTicketNum = Integer.parseInt(cmd.getOptionValue("s"));
            }
            if (cmd.hasOption("e")) {
                endTicketNum = Integer.parseInt(cmd.getOptionValue("e"));
            }
            if (cmd.hasOption("o")) {
                out = cmd.getOptionValue("o");
            }
            if (cmd.hasOption("f")) {
                htmlFilename = cmd.getOptionValue("f");
            }
            if (cmd.hasOption("l")) {
                login = cmd.getOptionValue("l");
            }
            if (cmd.hasOption("p")) {
                pwd = cmd.getOptionValue("p");
            }
            if (cmd.hasOption("c")) {
                configPath = cmd.getOptionValue("c");
            }

            Properties properties = new Properties();

            FileInputStream fis = new FileInputStream(configPath);
            properties.load(fis);

            String url = properties.getProperty("url");
            String path = properties.getProperty("path");
            String projectname = properties.getProperty("projectname");
            String pathtoscan = properties.getProperty("issuespath");

            CheckedIssueAnalyzer checkedIssueAnalyzer = new CheckedIssueAnalyzer(pathtoscan, projectname);
            ArrayList<Integer> checkedIssues = checkedIssueAnalyzer.getIssueNumbers();


            GithubIssueManager githubIssueManager = new GithubIssueManager(url, login, pwd);
            githubIssueManager.parse(startTicketNum, endTicketNum, checkedIssues);
            checkedIssueAnalyzer.addNumbers(githubIssueManager.getNumbers());


            BlameInspector blameInspector = new BlameInspector(path);
            blameInspector.loadFolderInfo(path, "");

            githubIssueManager.findAssignee(blameInspector);
            githubIssueManager.setAssignee();

            if (out.equals("screen")) {
                Screen screen = new Screen(githubIssueManager.getWhoAssignee());
                screen.out();
            }
            if (out.equals("html")) {
                HTMLPage htmlPage;
                if (htmlFilename != null) {
                    htmlPage = new HTMLPage(githubIssueManager.getWhoAssignee(), htmlFilename);
                } else {
                    htmlPage = new HTMLPage(githubIssueManager.getWhoAssignee());
                }
                htmlPage.out();
            }
        } catch (IOException | IssueTrackerException | GitException ex) {
            ex.printStackTrace();
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("blamer", options);
        } catch (NullPointerException e){
            e.printStackTrace();
        } catch (CheckedIssueException e){
            e.printStackTrace();
        }
    }
}
