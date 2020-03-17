package org.suai.blamer;

import org.suai.blamer.git.BlameInspector;
import org.suai.blamer.git.GitException;
import org.suai.blamer.issueTracker.GitIssueManager;
import org.suai.blamer.issueTracker.IssueTrackerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main{

    public static void main(String[] args) throws IOException, InterruptedException, IssueTrackerException {

        int ticket_num = -1;
        try {
            if(!args[0].isEmpty()){
                ticket_num = Integer.parseInt(args[0]);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
            properties.load(fis);
        } catch (IOException e) {
            e.getMessage();
        }

        String url = properties.getProperty("url");
        String path = properties.getProperty("path");
        GitIssueManager issueManager = new GitIssueManager(url);
        try {
            issueManager.parse();
        }catch (IssueTrackerException ex){
            ex.printStackTrace();
        }
        if (ticket_num != -1) {
            System.out.println(issueManager.getTicket(ticket_num));
        }

        BlameInspector blameInspector = new BlameInspector(path);
        blameInspector.loadFolderInfo(path);

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

        StackTrace stackTrace =  new StackTrace();
        stackTrace.getLines(tmp_stack, blameInspector);
        System.out.println("The name of the function that threw the exception : " + stackTrace.getFrame(0).getFunctionName());
    }
}
