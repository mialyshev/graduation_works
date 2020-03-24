package org.suai.blamer;

import org.suai.blamer.git.BlameInspector;

import java.io.IOException;
import java.util.ArrayList;

public class StackTrace {
    private ArrayList<StackFrame> stackFrames;

    public StackTrace(){
        stackFrames = new ArrayList<>();
    }

    public void getLines(String str, BlameInspector blameInspector) throws IOException{
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
            StackFrame stackFrame = new StackFrame();
            stackFrame.getStackInfo(stringBuilder.toString(), blameInspector);
            if (stackFrame.getCurString() != null) {
                stackFrames.add(stackFrame);
            }
            i = str.indexOf("at", i);
        }
    }

    public StackFrame getFrame(int num){
        return stackFrames.get(num);
    }
}
