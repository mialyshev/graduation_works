package org.suai.blamer.issueTracker.ticket;

import java.lang.reflect.Field;

public class Milestone {
    public String url;
    public String html_url;
    public String labels_url;
    public String id;
    public String node_id;
    public String number;
    public String state;
    public String title;
    public String description;
    public User creator;
    public String open_issues;
    public String closed_issues;
    public String created_at;
    public String updated_at;
    public String closed_at;
    public String due_on;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        Field[] fields = this.getClass().getFields();
        for(Field field: fields){
            try {
                stringBuilder.append("\t" + field.getName() + ": " + field.get(this) + "\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
