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


    private boolean checkStringForNum(String curstring) {
        for (int i = 0; i < curstring.length(); i++) {
            if (curstring.charAt(i) < '0' || curstring.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public void getStackInfo(String stackTrace, Map<String, String> fileInfo, String repoPath) {
        logger.info("Parsing stack frame : " + stackTrace);
        int i = stackTrace.indexOf(')') - 1;
        if (i < 0) {
            return;
        }
        if (stackTrace.charAt(i) < '0' || stackTrace.charAt(i) > '9') {
            return;
        }
        logger.info("Search a string number");
        boolean uknown = false;
        StringBuilder stringBuilder = new StringBuilder();
        while (stackTrace.charAt(i) != ':') {
            stringBuilder.append(stackTrace.charAt(i));
            i--;
        }
        String tmpnum = stringBuilder.reverse().toString();
        if (checkStringForNum(tmpnum)) {
            numString = Integer.parseInt(tmpnum);
        } else {
            return;
        }


        logger.info("Search a filename");
        i--;
        stringBuilder = new StringBuilder();
        while (stackTrace.charAt(i) != '(') {
            stringBuilder.append(stackTrace.charAt(i));
            i--;
        }
        fileName = stringBuilder.reverse().toString();
        if (!fileName.contains(".java")) {
            fileName = "Unknown Source";
            uknown = true;
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
