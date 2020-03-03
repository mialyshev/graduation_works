package programs;

import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    private static String makeUrl(String url, Boolean clone){
        StringBuilder cur_url = new StringBuilder();
        if (clone){
            cur_url.append(url + ".git");
            return cur_url.toString();
        }
        cur_url.append("https://api.github.com/repos/");
        int info_index = url.indexOf("github.com");
        int fromindex = info_index + 1;
        info_index = url.indexOf('/', fromindex) + 1;
        while (info_index != url.length()){
            cur_url.append(url.charAt(info_index));
            info_index++;
        }
        cur_url.append("/issues");
        return cur_url.toString();
    }


    public static void main(String[] args) throws GitAPIException, IOException, InterruptedException {
        String orig_url = args[0];
        String api_url = makeUrl(orig_url, false);
        String clone_url = makeUrl(orig_url, true);
        int num = 0;
        IssueManager issueManager = null;
        BlameInspector blameInspector = null;
        StackTrace stackTrace = null;

        Scanner scanner = new Scanner(System.in);
        boolean flag = false;

        String tmp_stack = "javax.servlet.ServletException: Произошло что–то ужасное\n" +
                "   at com.example.myproject.Article.findWord(Article.java:88)\n" +
                "   at org.mortbay.jetty.servlet.ArticleController.editArticle(ArticleController.java:138)\n" +
                "   at com.example.myproject.UserController.userSave(UserController.java:57)";

        while(true){
            if (flag){
                break;
            }
            System.out.println("Enter a function by entering a number:\n" +
                    "1 - Download all tickets\n" +
                    "2 - Clone and show files\n" +
                    "3 - Show tickets\n" +
                    "4 - StackTrace analyze\n" +
                    "-1 - Exit program");

            num = scanner.nextInt();
            switch (num){
                case (1):
                    issueManager = new IssueManager(api_url);
                    System.out.println("All tickets download. Now you can view them");
                    break;
                case (2):
                    blameInspector = new BlameInspector(clone_url);
                    break;
                case (3):
                    System.out.println("Ticket numbers:");
                    issueManager.outNumbers();
                    System.out.println("Enter ticket number:");
                    num = scanner.nextInt();
                    System.out.println(issueManager.getTicket(num));
                    break;
                case (4):
                    stackTrace = new StackTrace(tmp_stack, blameInspector);
                    break;
                case (-1):
                    flag = true;
                    break;
                default:
                    System.out.println("You enter wrong number. Thy again");
            }
        }
    }
}
