package programs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

        Scanner scanner = new Scanner(System.in);
        boolean flag = false;

        while(true){
            if (flag){
                break;
            }
            System.out.println("Enter a function by entering a number:\n" +
                    "1 - Download all tickets\n" +
                    "2 - Clone and show files\n" +
                    "3 - Show tickets\n" +
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
                    if (issueManager == null){
                        System.out.println("On first you need download tickets");
                        break;
                    }
                    System.out.println("Ticket numbers:");
                    issueManager.outNumbers();
                    System.out.println("Enter ticket number:");
                    num = scanner.nextInt();
                    while(!issueManager.checkNumber(num)){
                        System.out.println("Wrong number, enter new:");
                        num = scanner.nextInt();
                    }
                    issueManager.getTicket(num).outTicketInfo();
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
