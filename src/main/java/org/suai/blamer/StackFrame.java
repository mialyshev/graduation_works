package org.suai.blamer;

import org.suai.blamer.git.BlameInspector;

import java.io.*;



public class StackFrame {
    private String functionName = null;
    private String fileName = null;
    private String curString = null;

    public StackFrame(){
    }

    public void getStackInfo(String stackTrace, BlameInspector blameInspector) {
        boolean uknown = false;
        int i = stackTrace.indexOf('(') + 1;
        int start = i - 2;
        StringBuilder stringBuilder = new StringBuilder();
        while (stackTrace.charAt(i) != ':'){
            if (stackTrace.charAt(i) == ')'){
                uknown = true;
                break;
            }
            stringBuilder.append(stackTrace.charAt(i));
            i++;
        }
        if (uknown){
            this.fileName = "Unknown Source";
        }
        else {
            this.fileName = stringBuilder.toString();
        }

        int numString = 0;
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
        while (stackTrace.charAt(start) != '.'){
            stringBuilder.append(stackTrace.charAt(start));
            start--;
        }
        this.functionName = stringBuilder.reverse().toString();


        if(!uknown) {
            String path = blameInspector.getFilePath(this.fileName);
            if (path != null) {
                int curLine = 1;
                try {
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
                } catch (IOException ex) {
                    ex.getMessage();
                }
            }
        }
    }

    public String getCurString() {
        if (curString == null){
            return null;
        }
        return curString;
    }

    public String getFunctionName() {
        return functionName;
    }
}
