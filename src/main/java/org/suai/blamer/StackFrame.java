package org.suai.blamer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;


public class StackFrame {
    private String functionName;
    private String fileName;
    private String curString;
    private int numString;
    private static Logger logger = Logger.getLogger(StackFrame.class.getName());

    public StackFrame(){
    }

    public void getStackInfo(String stackTrace, Map<String,String> fileInfo, String repoPath) throws IOException {
        logger.info("Parsing stack frame : " + stackTrace);
        boolean uknown = false;
        int i = stackTrace.indexOf('(') + 1;
        int start = i - 2;
        StringBuilder stringBuilder = new StringBuilder();
        try {
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
                while (stackTrace.charAt(i) != ')') {
                    stringBuilder.append(stackTrace.charAt(i));
                    i++;
                }
                numString = Integer.parseInt(stringBuilder.toString());
            }

            logger.info("Search a name of function");
            stringBuilder = new StringBuilder();
            while (stackTrace.charAt(start) != '.') {
                stringBuilder.append(stackTrace.charAt(start));
                start--;
            }
            this.functionName = stringBuilder.reverse().toString();

            logger.info("Search for a string with the received number");
            if (!uknown) {
                String path;
                if (fileInfo.containsKey(fileName)) {
                    path = repoPath + "/" + fileInfo.get(fileName);
                } else {
                    path = null;
                }
                if (path != null) {
                    int curLine = 1;
                    FileReader fr = new FileReader(path);
                    BufferedReader reader = new BufferedReader(fr);
                    String line = reader.readLine();
                    while (line != null) {
                        if (curLine == numString) {
                            this.curString = line;
                            break;
                        }
                        line = reader.readLine();
                        curLine++;
                    }
                }
            }
        }catch (IOException ex) {
            throw new IOException(ex);
        }
    }

    public String getCurString() {
        if (curString == null){
            return null;
        }
        return curString;
    }


    public int getNumString() {
        return numString;
    }


    public String getFileName() {
        return fileName;
    }


}
