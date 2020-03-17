package org.suai.blamer;

import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.git.GitException;
import org.suai.blamer.issueTracker.GitIssueManager;
import org.suai.blamer.issueTracker.IssueTrackerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class Main{

    public static void main(String[] args) throws IOException, InterruptedException, IssueTrackerException {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            e.getMessage();
        }

        String url = properties.getProperty("url");
        String path = properties.getProperty("path");
        int num = 0;
        GitIssueManager issueManager = null;
        BlameInspector blameInspector = null;
        StackTrace stackTrace = null;

        Scanner scanner = new Scanner(System.in);
        boolean flag = false;

        String tmp_stack = "javax.servlet.ServletException: Произошло что–то ужасное\n" +
                "   at java.lang.reflect.WeakCache$Factory.get(WeakCache.java:230)\n" +
                "   at java.lang.reflect.WeakCache.get(WeakCache.java:127)\n" +
                "   at java.lang.reflect.Proxy.getProxyClass0(Proxy.java:419)\n" +
                "   at java.lang.reflect.Proxy.getProxyClass(Proxy.java:371)\n" +
                "   at com.example.myproject.Article.findWord(Article.java:88)\n" +
                "   at com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory$Builder.as(PipelineOptionsFactory.java:284)\n" +
                "   at datasplash.core$make_pipeline.invoke(core.clj:661)\n" +
                "   at gcptest2.core$get_word_count_pipeline.invoke(core.clj:39)\n" +
                "   at gcptest2.core$run_dataflow_job.invoke(core.clj:72)\n" +
                "   at com.alex4321.botweb.Application.main(Application.java:23)\n" +
                "   at org.mortbay.jetty.servlet.ArticleController.editArticle(ArticleController.java:138)\n" +
                "   at com.example.myproject.UserController.userSave(UserController.java:57)";

        while(true){
            if (flag){
                break;
            }
            System.out.println("Enter a function by entering a number:\n" +
                    "1 - Download all tickets\n" +
                    "2 - Load and show files\n" +
                    "3 - Show tickets\n" +
                    "4 - StackTrace analyze\n" +
                    "5 - blame file\n" +
                    "-1 - Exit program");

            num = scanner.nextInt();
            switch (num){
                case (1):
                    issueManager = new GitIssueManager(url);
                    try {
                        issueManager.parse();
                    }catch (IssueTrackerException ex){
                        ex.printStackTrace();
                    }

                    System.out.println("All tickets download. Now you can view them");
                    break;
                case (2):
                    blameInspector = new BlameInspector(path);
                    blameInspector.loadFolderInfo(path);
                    blameInspector.viewFolderFiles(path, 0);
                    break;

                case (3):
                    System.out.println("Ticket numbers:");
                    issueManager.outNumbers();
                    System.out.println("Enter ticket number:");
                    num = scanner.nextInt();
                    System.out.println(issueManager.getTicket(num));
                    break;
                case (4):
                    stackTrace = new StackTrace();
                    stackTrace.getLines(tmp_stack, blameInspector);
                    System.out.println("The name of the function that threw the exception : " + stackTrace.getFrame(0).getFunctionName());
                    break;
                case (5):
                    String filename = "MvcConfig.java";
                    try {
                        blameInspector.blame(filename);
                    }catch (GitException ex){
                        ex.printStackTrace();
                    }
                case (-1):
                    flag = true;
                    break;
                default:
                    System.out.println("You enter wrong number. Thy again");
            }
        }
    }
}
