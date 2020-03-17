package org.suai.blamer;

import org.suai.blamer.git.BlameInspector;

import java.io.*;



public class StackFrame {
    private String functionName;
    private String fileName;
    private String curString;

    public StackFrame(){
    }

    public void getStackInfo(String stackTrace, BlameInspector blameInspector) throws IOException {
        int i = stackTrace.indexOf('(') + 1;
        int start = i - 2;
        StringBuilder stringBuilder = new StringBuilder();
        while (stackTrace.charAt(i) != ':'){
            stringBuilder.append(stackTrace.charAt(i));
            i++;
        }
        this.fileName = stringBuilder.toString();

        i++;
        stringBuilder = new StringBuilder();
        while (stackTrace.charAt(i) != ')'){
            stringBuilder.append(stackTrace.charAt(i));
            i++;
        }
        int numString = Integer.parseInt(stringBuilder.toString());

        stringBuilder = new StringBuilder();
        while (stackTrace.charAt(start) != '.'){
            stringBuilder.append(stackTrace.charAt(start));
            start--;
        }
        this.functionName = stringBuilder.reverse().toString();


        String path = blameInspector.getFilePath(this.fileName);
        if (path != null) {
            int curLine = 1;
            try {
                FileReader fr = new FileReader(path);
                BufferedReader reader = new BufferedReader(fr);
                String line = reader.readLine();
                while (line != null) {
                    if (curLine == numString){
                        this.curString = line;
                        break;
                    }
                    line = reader.readLine();
                    curLine ++;
                }
            }
            catch(IOException ex){
                ex.getMessage();
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
