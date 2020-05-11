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
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class Main {
    private static Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("s", "start", true, "Ticket start number. Default = 0");
        options.addOption("e", "end", true, "Ticket end number. Default = 100000");
        options.addOption("o", "out", true, "You can write either 'screen' or 'html'. Default = screen");
        options.addRequiredOption("t", "token", true, "Personal access token from your github for authorization");
        options.addOption("f", "file", true, "The name of the file to display in html. Default = output.html");
        options.addOption("c", "config properties", true, "Path to file 'config.properties'");
        options.addOption("m", "mode", true, "Program mode. Default = report");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        logger.info("Parsing command line arguments");
        try {
            cmd = parser.parse(options, args);

            int startTicketNum = 0;
            int endTicketNum = 100000;
            String out = "screen";
            String htmlFilename = null;
            String token = null;
            String configPath = "config.properties";
            String mode = "report";

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
            if (cmd.hasOption("t")) {
                token = cmd.getOptionValue("t");
            }
            if (cmd.hasOption("c")) {
                configPath = cmd.getOptionValue("c");
            }
            if (cmd.hasOption("m")) {
                mode = cmd.getOptionValue("m");
            }

            logger.info("Parsing config.properties file");
            Properties properties = new Properties();

            FileInputStream fis = new FileInputStream(configPath);
            properties.load(fis);

            String url = properties.getProperty("url");
            String path = properties.getProperty("path");
            String projectname = properties.getProperty("projectname");
            String pathtoscan = properties.getProperty("issuespath");

            CheckedIssueAnalyzer checkedIssueAnalyzer = new CheckedIssueAnalyzer(pathtoscan, projectname);
            List<Integer> checkedIssues = checkedIssueAnalyzer.getIssueNumbers();


            GithubIssueManager githubIssueManager = new GithubIssueManager(url, token);
            githubIssueManager.parse(startTicketNum, endTicketNum, checkedIssues);
            checkedIssueAnalyzer.addNumbers(githubIssueManager.getNumbers());


            BlameInspector blameInspector = new BlameInspector(path);
            logger.info("Start scanning folder on path : " + path);
            blameInspector.loadFolderInfo(path, "");

            githubIssueManager.findAssignee(blameInspector);
            if (mode == "autoassignee") {
                githubIssueManager.setAssignee();
            }


            if (mode == "report") {
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
            }
        } catch (IOException | IssueTrackerException | GitException ex) {
            ex.printStackTrace();
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("blamer", options);
        } catch (NullPointerException | CheckedIssueException e) {
            e.printStackTrace();
        }
    }
}
