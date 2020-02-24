package programs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
        ArrayList<String> arrayList = new ArrayList<String>();
        HttpURLConnection httpcon = (HttpURLConnection) new URL(url).openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
        String str = in.readLine();
        int fromindex = 0;
        int i = 0;
        while (i != -1){
            StringBuilder number_ticket = new StringBuilder();
            i = str.indexOf("number", fromindex);
            fromindex = i + 1;
            if (i != -1) {
                while (str.charAt(i) != ':') {
                    i++;
                }
                while (str.charAt(i) != ',') {
                    if (str.charAt(i) == ':'){
                        i++;
                        continue;
                    }
                    number_ticket.append(str.charAt(i));
                    i++;
                }
                numbers.add(Integer.parseInt(number_ticket.toString()));

                httpcon = (HttpURLConnection) new URL(url + '/' + number_ticket).openConnection();
                in = new BufferedReader(new InputStreamReader(httpcon.getInputStream()));
                arrayList.add(in.readLine());
            }
        }
        Iterator<String> iter = arrayList.iterator();
        while (iter.hasNext()){
            Ticket ticket = new Ticket();
            String cur_ticket = iter.next();
            Map<String, String> info = parseTicketInfo(cur_ticket, false, false, false, false);
            if (!info.isEmpty()){
                for (Map.Entry<String, String> entry : info.entrySet()) {
                    ticket.addTicketInfo(entry.getKey(), entry.getValue());
                }
            }

            Map<String, String> user_info = parseTicketInfo(cur_ticket, true, false, false, false);
            if (!user_info.isEmpty()){
                for (Map.Entry<String, String> entry : user_info.entrySet()) {
                    ticket.addUserInfo(entry.getKey(), entry.getValue());
                }
            }

            Map<String, String> pull_info = parseTicketInfo(cur_ticket, false, true, false, false);
            if (!pull_info.isEmpty()){
                for (Map.Entry<String, String> entry : pull_info.entrySet()) {
                    ticket.addPullRequestInfo(entry.getKey(), entry.getValue());
                }
            }

            Map<String, String> labels_info = parseTicketInfo(cur_ticket, false, false, true, false);
            if (!labels_info.isEmpty()){
                for (Map.Entry<String, String> entry : labels_info.entrySet()) {
                    ticket.addLabelsInfo(entry.getKey(), entry.getValue());
                }
            }

            Map<String, String> closed_info = parseTicketInfo(cur_ticket, false, false, false, true);
            if (!closed_info.isEmpty()){
                for (Map.Entry<String, String> entry : closed_info.entrySet()) {
                    ticket.addClosedInfo(entry.getKey(), entry.getValue());
                }
            }

            ticketpack.add(ticket);
        }
    }

    private Map<String, String> parseTicketInfo(String ticket, boolean user, boolean pull_request, boolean labels, boolean closed){
        Map<String, String> ticketinfo = new HashMap<String, String>();
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        int user_info = ticket.indexOf("\"user\"");
        int pull_requestinfo = ticket.indexOf("\"pull_request\"");
        int end_ticket = ticket.indexOf("\"closed_by\"");
        int labels_info = ticket.indexOf("\"labels\"");

        int fromindex = 0;
        int i = ticket.indexOf('"', fromindex) + 1;
        if (user){
            i = ticket.indexOf("\"login\":", fromindex) + 1;
            end_ticket = ticket.indexOf("\"site_admin\"", i);
        }

        if (pull_request){
            if (pull_requestinfo == -1){
                return ticketinfo;
            }
            i = ticket.indexOf(" \"pull_request\":", fromindex) + 26;
            end_ticket = ticket.indexOf("\"patch_url\"");
        }

        if (labels){
            i = labels_info + 12;
            end_ticket = ticket.indexOf("\"description\"");
            if (end_ticket == -1){
                return ticketinfo;
            }
        }

        if (closed){
            i = end_ticket + 12;
            if(ticket.charAt(i) != '{'){
                ticketinfo.put("closed_by", "null");
                return ticketinfo;
            }
            i += 2;
            end_ticket = ticket.indexOf("\"site_admin\"", i);
        }


        boolean isKeyCopy = true;
        boolean isValueCopy = false;
        boolean last = false;

        while (i != -1){
            if (i - 1 == user_info | i - 1 == pull_requestinfo){
                fromindex = i;
                i = ticket.indexOf("},", fromindex) + 3;
            }
            if (i - 1 == labels_info){
                fromindex = i;
                i = ticket.indexOf("],", fromindex) + 3;
            }
            if (i - 1 == end_ticket){
                last = true;
                if (!user & !labels & !pull_request & !closed){
                    break;
                }
            }
            if (isKeyCopy){
                while (ticket.charAt(i) != '"'){
                    key.append(ticket.charAt(i));
                    i++;
                }
                isKeyCopy = false;
                isValueCopy = true;
                i += 2;
                if (ticket.charAt(i) == '"'){
                    i += 1;
                }
            }
            if (isValueCopy){
                if(last){
                    while (ticket.charAt(i) != '}'){
                        value.append(ticket.charAt(i));
                        i++;
                    }
                }
                else{
                    while (ticket.charAt(i) != ','){
                        if (ticket.charAt(i) == '"' & ticket.charAt(i + 1) == ','){
                            i++;
                            break;
                        }
                        value.append(ticket.charAt(i));
                        i++;
                        if (ticket.charAt(i) == ',' & ticket.charAt(i + 1) != '"'){
                            value.append(ticket.charAt(i));
                            i++;
                        }
                    }
                }
            }
            if(last){
                i = -1;
                ticketinfo.put(key.toString(), value.toString());
                continue;
            }
            fromindex = i;
            i = ticket.indexOf('"', fromindex) + 1;
            ticketinfo.put(key.toString(), value.toString());
            key = new StringBuilder();
            value = new StringBuilder();
            isKeyCopy = true;

        }
        return ticketinfo;
    }

    public Ticket getTicket(int number){
        if (number < 0){
            throw new NullPointerException("You entered an invalid number");
        }else {
            Iterator<Ticket>iterator = ticketpack.iterator();
            while (iterator.hasNext()){
                Ticket cur_ticket = iterator.next();
                if(cur_ticket.getNumber() == number){
                    return cur_ticket;
                }
            }
        }
        throw new NullPointerException("You entered an invalid number");
    }


    public ArrayList<Integer> getNumbers(){
        return numbers;
    }

    public void outNumbers(){
        Iterator<Integer> iterator = numbers.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
