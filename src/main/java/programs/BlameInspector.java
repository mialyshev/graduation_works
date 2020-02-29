package programs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;


public class BlameInspector {
    String url;
    String path;

    public BlameInspector(String url) throws IOException,
            InterruptedException {
        this.url = url;
        path = "./" + getName(url);
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
        return str.reverse().toString();
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

        String path = getPath(stackFrame.getFileName(), this.path);

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

    public String getPath(String fileName, String curPath) throws IOException{
        File Folder = new File(curPath);
        for (File file : Folder.listFiles())
        {
            if(file.getName().equals(fileName)){
                return file.getAbsolutePath();
            }
            if (file.isDirectory()){
                String path = getPath(fileName, file.getAbsolutePath());
                if (path != null){
                    return path;
                }
            }
        }
        return null;
    }
}