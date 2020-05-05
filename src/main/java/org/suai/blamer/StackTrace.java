package org.suai.blamer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

public class StackTrace {
    private ArrayList<StackFrame> stackFrames;
    private static Logger logger = Logger.getLogger(StackTrace.class.getName());
    private String repoPath;

    public StackTrace(String path) {
        repoPath = path;
        stackFrames = new ArrayList<>();
    }

    public void getLines(String str, Map<String,String> fileInfo) throws IOException {
        logger.info("Parsing a stacktrace to glass stack frames");
        int i = str.indexOf("at");
        while(i != -1) {
            StringBuilder stringBuilder = new StringBuilder();
            while(str.charAt(i) != '\n') {
                stringBuilder.append(str.charAt(i));
                i++;
                if(i >= str.length()) {
                    break;
                }
            }
            stringBuilder.append('\n');
            StackFrame stackFrame = new StackFrame();
            stackFrame.getStackInfo(stringBuilder.toString(), fileInfo, repoPath);
            if(stackFrame.getisProject() != false) {
                stackFrames.add(stackFrame);
            }
            i = str.indexOf("at", i);
        }

    }

    public StackFrame getFrame(int num){
        if(!stackFrames.isEmpty() && stackFrames.size() > num) {
            return stackFrames.get(num);
        }
        return null;
    }
}
