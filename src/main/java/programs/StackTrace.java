package programs;

import java.io.IOException;
import java.util.ArrayList;

public class StackTrace {
    ArrayList<StackFrame> stackFrames;

    public StackTrace(String stack, BlameInspector blameInspector) throws IOException, InterruptedException {
        stackFrames = new ArrayList<>();
        getLines(stack, blameInspector);
    }

    public void getLines(String str, BlameInspector blameInspector) throws IOException, InterruptedException {
        int i = str.indexOf("at");

        while(i != -1) {
            StringBuilder stringBuilder = new StringBuilder();
            while (str.charAt(i) != '\n') {
                stringBuilder.append(str.charAt(i));
                i++;
                if (i >= str.length()){
                    break;
                }
            }
            StackFrame stackFrame = new StackFrame(stringBuilder.toString(), blameInspector);
            if (stackFrame.getCur_string() != null) {
                stackFrames.add(stackFrame);
            }
            i = str.indexOf("at", i);
        }
    }

    public StackFrame getFrame(int num){
        return stackFrames.get(num);
    }
}
