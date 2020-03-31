package org.suai.blamer.issueTracker.ticket;

import java.lang.reflect.Field;
import java.util.List;

public class Ticket {
    public String url;
    public String repository_url;
    public String labels_url;
    public String comments_url;
    public String events_url;
    public String html_url;
    public String id;
    public String node_id;
    public String number;
    public String title;
    public User user;
    public List<Labels> labels;
    public String state;
    public String locked;
    public User assignee;
    public List<User> assignees;
    public Milestone milestone;
    public String comments;
    public String created_at;
    public String updated_at;
    public String closed_at;
    public String author_association;
    public String body;
    public User closed_by;
    public String active_lock_reason;
    public PullRequest pull_request;


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        Field[] fields = this.getClass().getFields();
        for(Field field: fields){
            try {
                stringBuilder.append(field.getName() + ": " + field.get(this) + "\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    public void setAssignee(User user){
        assignee = user;
    }

    public String getBody() {
        return body;
    }

    public String getNumber() {
        return number;
    }
}

