package org.suai.blamer.issuetracker.ticket;

import java.lang.reflect.Field;

public class Labels {
    public String id;
    public String node_id;
    public String url;
    public String name;
    public String color;
    public String defaultinfo = "true";
    public String description;

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        Field[] fields = this.getClass().getFields();
        for (Field field : fields) {
            try {
                stringBuilder.append("\t" + field.getName() + ": " + field.get(this) + "\n");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
