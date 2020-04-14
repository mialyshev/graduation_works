package org.suai.blamer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class StackFrame {
    private String functionName;
    private String fileName;
    private String curString;
    private int numString;

    public StackFrame(){
    }

    public void getStackInfo(String stackTrace, Map<String,String> fileInfo, String repoPath) throws IOException {
        boolean uknown = false;
        int i = stackTrace.indexOf('(') + 1;
        int start = i - 2;
        StringBuilder stringBuilder = new StringBuilder();
        try {
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

            if (!uknown) {
                i++;
                stringBuilder = new StringBuilder();
                while (stackTrace.charAt(i) != ')') {
                    stringBuilder.append(stackTrace.charAt(i));
                    i++;
                }
                numString = Integer.parseInt(stringBuilder.toString());
            }

            stringBuilder = new StringBuilder();
            while (stackTrace.charAt(start) != '.') {
                stringBuilder.append(stackTrace.charAt(start));
                start--;
            }
            this.functionName = stringBuilder.reverse().toString();


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
