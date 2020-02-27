package programs;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;


public class BlameInspector {
    String url;
    String path;
    public BlameInspector(String url) throws IOException, InterruptedException {
        this.url = url;
        path = "./" + getName(url);
        try {
            cloneRepo(url);
        }catch (GitAPIException ex){
            System.out.println(ex.getMessage());
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


}
