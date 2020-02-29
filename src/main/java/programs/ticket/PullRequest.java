package programs.ticket;

import java.lang.reflect.Field;

public class PullRequest {
    public String url;
    public String html_url;
    public String diff_url;
    public String patch_url;

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
