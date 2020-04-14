package org.suai.blamer.check;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class CheckedIssueAnalyzer {
    File file;
    String filePath;
    String projectName;
    int stringNum;
    ArrayList<Integer> checkedNum;

    public CheckedIssueAnalyzer(String path, String projectName) throws CheckedIssueException {
        this.filePath = path;
        file = new File(filePath);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
        }catch (IOException e){
            throw new CheckedIssueException("The file for the specified path (" + filePath + ") was not found");
        }
        stringNum = 0;
        this.projectName = projectName;
        checkedNum = new ArrayList<>();
    }

    public ArrayList<Integer> getIssueNumbers() throws CheckedIssueException {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            while (line != null) {
                if (getProjectName(line).equals(projectName)) {
                    break;
                }
                line = reader.readLine();
                stringNum++;
            }
            ArrayList<Integer> checkedIssues = new ArrayList<>();
            if (line == null) {
                if (stringNum > 0) {
                    stringNum++;
                }
            } else {
                int i = line.indexOf('[');
                while (line.charAt(i) != ']') {
                    i++;
                    StringBuilder stringBuilder = new StringBuilder();
                    while (line.charAt(i) != ';' & line.charAt(i) != ']') {
                        stringBuilder.append(line.charAt(i));
                        i++;
                    }
                    if (stringBuilder.toString().length() != 0) {
                        checkedNum.add(Integer.parseInt(stringBuilder.toString()));
                    }

                }
            }
            fileReader.close();
        }catch (IOException e){
            throw new CheckedIssueException("The open file for the specified path (" + filePath + ")");
        }
        return checkedNum;
    }

    public String getProjectName(String line){
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while (line.charAt(i) != ' '){
            stringBuilder.append(line.charAt(i));
            i++;
        }
        return stringBuilder.toString();
    }

    public void addNumbers(ArrayList<Integer> numbers) throws CheckedIssueException {
        try {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(projectName + " [");
            Iterator<Integer> iterator = checkedNum.iterator();
            boolean firstIteration = true;
            int curi = 0;
            while (iterator.hasNext()) {
                if (!firstIteration & curi != checkedNum.size()) {
                    stringBuilder.append(';');
                }
                int curnum = iterator.next();
                stringBuilder.append(String.valueOf(curnum));
                firstIteration = false;
                curi++;
            }
            if (numbers.size() != 0 & checkedNum.size() != 0) {
                stringBuilder.append(';');
            }
            iterator = numbers.iterator();
            firstIteration = true;
            curi = 0;
            while (iterator.hasNext()) {
                if (!firstIteration & curi != numbers.size()) {
                    stringBuilder.append(';');
                }
                int curnum = iterator.next();
                stringBuilder.append(String.valueOf(curnum));
                firstIteration = false;
                curi++;
            }
            stringBuilder.append(']');

            StringBuilder stringToFile = new StringBuilder();
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            if (line == null) {
                stringToFile.append(stringBuilder.toString());
            }
            int curLine = 0;
            while (line != null) {
                if (curLine == stringNum) {
                    stringToFile.append(stringBuilder.toString() + '\n');
                } else {
                    stringToFile.append(line + '\n');
                }
                line = reader.readLine();
                curLine++;
            }
            if (curLine < stringNum) {
                stringToFile.append(stringBuilder.toString());
            }
            PrintWriter out = new PrintWriter(file);
            out.print(stringToFile.toString());
            out.flush();
            out.close();
        }catch (IOException e){
            throw new CheckedIssueException(e);
        }
    }
}
