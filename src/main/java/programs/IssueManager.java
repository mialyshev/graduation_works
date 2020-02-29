package programs;

import com.google.gson.Gson;
import programs.ticket.Ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class IssueManager {
    ArrayList<Ticket> ticketpack;
    ArrayList<String> сollaborators;
    ArrayList<Integer> numbers;
    String url;

    public IssueManager(String url) throws IOException {
        ticketpack = new ArrayList<>();
        сollaborators = new ArrayList<>();
        numbers = new ArrayList<>();
        this.url = url;

        parse();
    }


    private void parse() throws IOException {
        HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
        String issue = in.readLine();
        int fromindex = 0;
        int i = 0;
        while (i != -1){
            StringBuilder number_ticket = new StringBuilder();
            i = issue.indexOf("number", fromindex);
            fromindex = i + 1;
            if (i != -1) {
                while (issue.charAt(i) != ':') {
                    i++;
                }
                while (issue.charAt(i) != ',') {
                    if (issue.charAt(i) == ':'){
                        i++;
                        continue;
                    }
                    number_ticket.append(issue.charAt(i));
                    i++;
                }
                numbers.add(Integer.parseInt(number_ticket.toString()));

                httpcon = (HttpURLConnection) new URL(url + '/' + number_ticket).openConnection();
                in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                String cur_issue = in.readLine();
                Gson gson = new Gson();
                Ticket ticket = gson.fromJson(cur_issue, Ticket.class);
                ticketpack.add(ticket);
            }
        }
        System.out.println("1");


    }

    public Ticket getTicket(int number){
        Iterator<Ticket>iterator = ticketpack.iterator();
        while (iterator.hasNext()){
            Ticket cur_ticket = iterator.next();
            if(Integer.parseInt(cur_ticket.number) == number){
                return cur_ticket;
            }
        }
        throw new NullPointerException("You entered an invalid number");
    }

    public ArrayList<Integer> getNumbers(){
        return numbers;
    }

    public void outNumbers(){
        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

    }

    public boolean checkNumber(int num){
        Iterator<Integer>iterator = numbers.iterator();
        while (iterator.hasNext()){
            if (iterator.next().equals(num)){
                return true;
            }
        }
        return false;
    }
}
