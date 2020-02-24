package programs;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static programs.Folder.out_bash;

public class BlameInspector {
    Folder folder;
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
        viewFolderFiles();
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
        Object obj = folder.findFile(filename);
        Repository existingRepo = null;
        if(obj != null){
            String c = obj.toString();
            SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
            existingRepo = new FileRepositoryBuilder()
                    .setGitDir(new File(path))
                    .build();
            BlameCommand blamer = new BlameCommand(existingRepo);
            BufferedReader br =  new BufferedReader(new FileReader(new File(c + "/" + filename)));
            BlameResult blame = blamer.setFilePath(c + "/" +filename).call();
            String line = "";
            for (int i = 0; (line= br.readLine())!=null ; i++) {
                // the blame at this point is null.
                PersonIdent person = blame.getSourceAuthor(i);
                System.out.println(person.getName() + ": "+ line);
            }
        }
        else{
            System.out.println("File not found");
        }
    }

    private void viewFolderFiles() throws IOException, InterruptedException {
        folder = new Folder(this.path, 0);
        folder.showFiles();
    }


}
