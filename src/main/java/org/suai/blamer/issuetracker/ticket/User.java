package org.suai.blamer.issuetracker.ticket;

import java.lang.reflect.Field;

public class User {
    public String login;
    public String id;
    public String node_id;
    public String avatar_url;
    public String gravatar_id;
    public String url;
    public String html_url;
    public String followers_url;
    public String following_url;
    public String gists_url;
    public String starred_url;
    public String subscriptions_url;
    public String organizations_url;
    public String repos_url;
    public String events_url;
    public String received_events_url;
    public String type;
    public String site_admin;
    public Permissions permissions;
    public String contributions;

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

    public String getLogin() {
        return login;
    }
}
