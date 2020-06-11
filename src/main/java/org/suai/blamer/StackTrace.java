package org.suai.blamer;

import java.util.ArrayList;
import java.util.Iterator;
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

    public void getLines(ArrayList<String> arrayList, Map<String, String> fileInfo) {
        logger.info("Parsing a stacktrace to glass stack frames");
        Iterator<String> iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            String curstring = iterator.next();
            StackFrame stackFrame = new StackFrame();
            stackFrame.getStackInfo(curstring, fileInfo, repoPath);
            if (stackFrame.getisProject() != false) {
                stackFrames.add(stackFrame);
            }
        }
    }

    public StackFrame getFrame(int num) {
        if (!stackFrames.isEmpty() && stackFrames.size() > num) {
            return stackFrames.get(num);
        }
        return null;
    }
}
