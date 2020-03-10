package programs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class BlameInspector {
    String url;
    String path;
    Map<String,String>fileInfo;

    public BlameInspector(String url) throws IOException,
            InterruptedException {
        this.url = url;
        fileInfo = new HashMap<>();
        path = "./" + getName(url);
        File Folder = new File(this.path);
        if (Folder.listFiles() != null){
            viewFolderFiles(this.path, 0);
            return;
        }
        try {
            cloneRepo(url);
        }catch (GitAPIException ex){
            ex.getMessage();
        }
        viewFolderFiles(this.path, 0);
    }

    public static String getName(String url){
        StringBuilder str = new StringBuilder();
        int i = url.length() - 1;
        while(url.charAt(i) != '/'){
            str.append(url.charAt(i));
            i--;
        }
        str = str.reverse();
        String string = str.toString();

        return string.replaceAll(".git", "");
    }

    private void cloneRepo(String url)throws GitAPIException {
        File folder = new File(path);
        folder.mkdir();
        Git git = Git.cloneRepository()
                .setURI(url)
                .setDirectory(folder)
                .call();
    }

    public void blame(String filename) throws IOException, InterruptedException, GitAPIException {
        String cur_path = fileInfo.get(filename);
        StringBuilder str = new StringBuilder();
        int k = cur_path.length() - 1;
        while(cur_path.charAt(k) != '/'){
            k--;
        }
        int j = 0;
        while (j != k){
            str.append(cur_path.charAt(j));
            j++;
        }
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repo = builder
                .findGitDir(new File(this.path))
                .readEnvironment()
                .build();

        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");

        final BlameResult result = new Git(repo).blame().setFilePath("MainController.java")
                .setTextComparator(RawTextComparator.WS_IGNORE_ALL).call();
        final RawText rawText = result.getResultContents();
        for (int i = 0; i < rawText.size(); i++) {
            final PersonIdent sourceAuthor = result.getSourceAuthor(i);
            final RevCommit sourceCommit = result.getSourceCommit(i);
            System.out.println(sourceAuthor.getName() +
                    (sourceCommit != null ? " - " + DATE_FORMAT.format(((long)sourceCommit.getCommitTime())*1000) +
                            " - " + sourceCommit.getName() : "") +
                    ": " + rawText.getString(i));
        }
    }

    private void viewFolderFiles(String path, int inc) throws IOException, InterruptedException {
        File folder = new File(path);
        for (File file : folder.listFiles())
        {
            for (int i = 0; i < inc; i++){
                System.out.print("\t");
            }
            if (file.isDirectory()){
                System.out.println(file.getName() + ':');
                viewFolderFiles(file.getAbsolutePath(), inc + 1);
            }
            else {
                System.out.println(file.getName());
                fileInfo.put(file.getName(), file.getAbsolutePath());
            }
        }
    }

    public String getPath() {
        return path;
    }

    public void getStackInfo(StackFrame stackFrame, String stackTrace) throws IOException {
        int i = stackTrace.indexOf('(') + 1;
        int start = i - 2;
        StringBuilder stringBuilder = new StringBuilder();
        while (stackTrace.charAt(i) != ':'){
            stringBuilder.append(stackTrace.charAt(i));
            i++;
        }
        stackFrame.setFileName(stringBuilder.toString());

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
        stackFrame.setFunctionName(stringBuilder.reverse().toString());
        String path = "";

        if(fileInfo.containsKey(stackFrame.getFileName())){
            path = fileInfo.get(stackFrame.getFileName());
        }
        else {
            return;
        }
        if (!path.isEmpty()) {
            int cur_line = 1;
            try {
                FileReader fr = new FileReader(new File(path));
                BufferedReader reader = new BufferedReader(fr);
                String line = reader.readLine();
                while (line != null) {
                    if (cur_line == numString){
                        stackFrame.setCur_string(line);
                        break;
                    }
                    line = reader.readLine();
                    cur_line ++;
                }
            }
            catch(IOException ex){
                ex.getMessage();
            }
        }else {
            throw new InterruptedIOException("File not found");
        }

    }

    public String getFilePath(String fileName, String curPath) throws IOException{
        File Folder = new File(curPath);
        for (File file : Folder.listFiles())
        {
            if(file.getName().equals(fileName)){
                return file.getAbsolutePath();
            }
            if (file.isDirectory()){
                String path = getFilePath(fileName, file.getAbsolutePath());
                if (path != null){
                    return path;
                }
            }
        }
        return null;
    }
}