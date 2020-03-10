package programs;

import java.io.*;

public class StackFrame {
    String functionName;
    String fileName;
    String cur_string;

    public StackFrame(String stackString, BlameInspector blameInspector) throws IOException, InterruptedException{
        blameInspector.getStackInfo(this, stackString);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCur_string(String cur_string) {
        this.cur_string = cur_string;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCur_string() {
        if (cur_string == null){
            return null;
        }
        return cur_string;
    }

    public String getFunctionName() {
        return functionName;
    }
}
