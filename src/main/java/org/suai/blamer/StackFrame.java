package org.suai.blamer;

import java.util.Map;
import java.util.logging.Logger;


public class StackFrame {
    private String fileName;
    private int numString;
    private static Logger logger = Logger.getLogger(StackFrame.class.getName());
    private boolean isProject;

    public StackFrame() {
        numString = -1;
        isProject = false;
    }

    public void getStackInfo(String stackTrace, Map<String,String> fileInfo, String repoPath) {
        logger.info("Parsing stack frame : " + stackTrace);
        boolean uknown = false;
        int i = stackTrace.indexOf('(') + 1;
        StringBuilder stringBuilder = new StringBuilder();
        if (i == 0){
            return;
        }

        logger.info("Search a filename");
        while (stackTrace.charAt(i) != ':') {
            if (stackTrace.charAt(i) == ')') {
                uknown = true;
                break;
            }
            stringBuilder.append(stackTrace.charAt(i));
            i++;

        }
        if (uknown) {
            this.fileName = "Unknown Source";
        } else {
            this.fileName = stringBuilder.toString();
        }
        logger.info("Search a string number");
        if (!uknown) {
            i++;
            stringBuilder = new StringBuilder();
            while (stackTrace.charAt(i) != ')' && stackTrace.charAt(i) != '\n') {
                stringBuilder.append(stackTrace.charAt(i));
                i++;
            }
            numString = Integer.parseInt(stringBuilder.toString());
        }
        if (!uknown) {
            String path = null;
            if (fileInfo.containsKey(fileName)) {
                path = repoPath + "/" + fileInfo.get(fileName);
            }
            if (path != null) {
                isProject = true;
            }
        }
    }


    public int getNumString() {
        return numString;
    }


    public String getFileName() {
        return fileName;
    }

    public boolean getisProject() {
        return isProject;
    }


}
