package org.suai.blamer;

import org.apache.commons.cli.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.git.GitException;
import org.suai.blamer.issuetracker.GithubIssueManager;
import org.suai.blamer.issuetracker.IssueTrackerException;
import org.suai.blamer.output.HTMLPage;
import org.suai.blamer.output.Screen;

import java.io.IOException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpClient;
import java.util.Arrays;
import java.util.Properties;

public class Main{
    public static void main(String[] args) throws IOException {
        Options options = new Options();
        options.addRequiredOption("s", "start", true, "Ticket start number");
        options.addRequiredOption("e", "end", true, "Ticket end number");
        options.addRequiredOption("o", "out", true, "You can write either 'screen' or 'html'");
        options.addOption("f", "file", true, "The name of the file to display in html");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

            int startTicketNum = -1;
            int endTicketNum = -1;
            String out = null;
            String htmlFilename = null;

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

            System.out.println(startTicketNum + " " + endTicketNum + " " + out + " " + htmlFilename);

            Properties properties = new Properties();

            InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties");
            properties.load(input);


            String url = properties.getProperty("url");
            String path = properties.getProperty("path");

            GithubIssueManager githubIssueManager = new GithubIssueManager(url);
            githubIssueManager.parse(startTicketNum, endTicketNum);


            BlameInspector blameInspector = new BlameInspector(path);
            blameInspector.loadFolderInfo(path, "");

            githubIssueManager.findAssignee(blameInspector);


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
            pe.printStackTrace();
        }



    }
}
