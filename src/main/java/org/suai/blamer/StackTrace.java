package org.suai.blamer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class StackTrace {
    private ArrayList<StackFrame> stackFrames;
    String repoPath;

    public StackTrace(String path){
        stackFrames = new ArrayList<>();
        repoPath = path;
    }

    public void getLines(String str, Map<String,String> fileInfo) throws IOException{
        int i = str.indexOf("at");
        try {
            while (i != -1) {
                StringBuilder stringBuilder = new StringBuilder();
                while (str.charAt(i) != '\n') {
                    stringBuilder.append(str.charAt(i));
                    i++;
                    if (i >= str.length()) {
                        break;
                    }
                }
                StackFrame stackFrame = new StackFrame();
                stackFrame.getStackInfo(stringBuilder.toString(), fileInfo, repoPath);
                if (stackFrame.getCurString() != null) {
                    stackFrames.add(stackFrame);
                }
                i = str.indexOf("at", i);
            }
        }catch (IOException ex){
            throw new IOException(ex);
        }
    }

    public StackFrame getFrame(int num){
        if (stackFrames.size() != 0 & stackFrames.size() > num){
            return stackFrames.get(num);
        }
        return null;
    }
}
