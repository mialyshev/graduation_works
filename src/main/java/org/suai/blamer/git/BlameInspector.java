package org.suai.blamer.git;


import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.ObjectId;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class BlameInspector {

    private static Logger logger = Logger.getLogger(BlameInspector.class.getName());
    private String path;
    private Map<String,String> fileInfo;

    public BlameInspector(String path){
        this.path = path;
        fileInfo = new HashMap<>();
    }


    public String blame(String filename, int stringNum) throws GitException{
        logger.info("Try to blame file with name : " + filename);
        Git git;
        ObjectId commitID;
        BlameResult blameResult;
        String blamedUserName;
        try{
            logger.info("Analyze git repository");
            git = Git.open(new File(this.path + "/.git"));
            commitID = git.getRepository().resolve("HEAD");
            BlameCommand cmd = new BlameCommand(git.getRepository());
            cmd.setStartCommit(commitID);
            cmd.setFilePath(getFilePathInRepo(filename));
            blameResult = cmd.call();
            logger.info("Try to get name of author");
            blamedUserName = blameResult.getSourceAuthor(stringNum - 1 ).getName();
        }catch (GitAPIException | IOException ex){
            throw new GitException(ex);
        }
        return blamedUserName;
    }

    public void loadFolderInfo(String absPath, String repoPath){
        File folder = new File(absPath);
        for (File file : folder.listFiles())
        {
            String curPath = "";
            if (repoPath == ""){
                curPath = file.getName();
            }else {
                curPath = repoPath + "/" + file.getName();
            }
            if (file.isDirectory()){
                loadFolderInfo(file.getAbsolutePath(), curPath);
            }
            else {
                fileInfo.put(file.getName(), curPath);
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


    public String getFilePathInRepo(String fileName){
        if (fileInfo.containsKey(fileName)){
            return fileInfo.get(fileName);
        }
        return null;
    }


    public String getFilePath(String fileName){
        if (fileInfo.containsKey(fileName)){
            return this.path + "/" + fileInfo.get(fileName);
        }
        return null;
    }


    public Map<String, String> getFileInfo() {
        return fileInfo;
    }

    public String getPath() {
        return path;
    }

}