package programs;

import java.io.IOException;
import java.util.ArrayList;

public class StackTrace {
    ArrayList<StackFrame> stackFrames;

    public StackTrace(String stack, BlameInspector blameInspector) throws IOException, InterruptedException {
        stackFrames = new ArrayList<>();
        ArrayList<String> lineStack = getLines(stack);
        for(String str : lineStack){
            stackFrames.add(new StackFrame(str, blameInspector));
        }
    }

    public ArrayList<String> getLines(String str){
        int i = str.indexOf("at");
        ArrayList<String>arrayList = new ArrayList<>();
        while(i != -1) {
            StringBuilder stringBuilder = new StringBuilder();
            while (str.charAt(i) != '\n') {
                stringBuilder.append(str.charAt(i));
                i++;
                if (i >= str.length()){
                    break;
                }
            }
            arrayList.add(stringBuilder.toString());
            i = str.indexOf("at", i);
        }
        return arrayList;
    }
}
