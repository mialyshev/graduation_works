package org.suai.blamer.git;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class BlameInspector {
    private String path;
    private Map<String,String>fileInfo;

    public BlameInspector(String path){
        this.path = path;
        fileInfo = new HashMap<>();
    }


    public void blame(String filename) throws GitException {
        String curPath = fileInfo.get(filename);
        StringBuilder str = new StringBuilder();
        int k = curPath.length() - 1;
        while(curPath.charAt(k) != '/'){
            k--;
        }
        int j = 0;
        while (j != k){
            str.append(curPath.charAt(j));
            j++;
        }

        Repository repo = null;
        try {
            repo = new FileRepositoryBuilder()
                    .findGitDir(new File(this.path))
                    .readEnvironment()
                    .setWorkTree(new File(str.toString()))
                    .build();
        }catch (IOException ex){
            throw new GitException(ex);
        }


        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd HH:mm");
        String [] strings = repo.getWorkTree().list();


        BlameResult result = null;
        try {
            result = new Git(repo).blame().setFilePath(filename)
                    .setTextComparator(RawTextComparator.WS_IGNORE_ALL).call();
        }catch (GitAPIException ex){
            throw new GitException(ex);
        }

        RawText rawText = result.getResultContents();
        for (int i = 0; i < rawText.size(); i++) {
            final PersonIdent sourceAuthor = result.getSourceAuthor(i);
            final RevCommit sourceCommit = result.getSourceCommit(i);
            System.out.println(sourceAuthor.getName() +
                    (sourceCommit != null ? " - " + DATE_FORMAT.format(((long)sourceCommit.getCommitTime())*1000) +
                            " - " + sourceCommit.getName() : "") +
                    ": " + rawText.getString(i));
        }
    }

    public void loadFolderInfo(String path){
        File folder = new File(path);
        for (File file : folder.listFiles())
        {
            if (file.isDirectory()){
                loadFolderInfo(file.getAbsolutePath());
            }
            else {
                fileInfo.put(file.getName(), file.getAbsolutePath());
            }
        }
    }

    public void viewFolderFiles(String path, int inc){
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


    public String getFilePath(String fileName, String curPath){
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

    public String getFilePath(String fileName){
        if (fileInfo.containsKey(fileName)){
            return fileInfo.get(fileName);
        }
        return null;
    }

}