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
    private Map<String, String> fileInfo;
    Git git;
    ObjectId commitID;

    public BlameInspector(String path) throws IOException {
        this.path = path;
        fileInfo = new HashMap<>();
        git = Git.open(new File(this.path + "/.git"));
        commitID = git.getRepository().resolve("HEAD");
    }


    public String blame(String filename, int stringNum) throws GitException {
        logger.info("Try to blame file with name : " + filename);
        BlameResult blameResult;
        String blamedUserName;
        try {
            BlameCommand cmd = new BlameCommand(this.git.getRepository());
            cmd.setStartCommit(this.commitID);
            cmd.setFilePath(getFilePathInRepo(filename));
            blameResult = cmd.call();
            logger.info("Try to get name of author");
            blamedUserName = blameResult.getSourceAuthor(stringNum - 1).getName();
        } catch (GitAPIException | NullPointerException ex) {
            throw new GitException(ex);
        } catch (ArrayIndexOutOfBoundsException e) {
            return "-1";
        }
        return blamedUserName;
    }

    public void loadFolderInfo(String absPath, String repoPath) {
        File folder = new File(absPath);
        for (File file : folder.listFiles()) {
            String curPath = "";
            if (repoPath == "") {
                curPath = file.getName();
            } else {
                curPath = repoPath + "/" + file.getName();
            }
            if (file.isDirectory()) {
                loadFolderInfo(file.getAbsolutePath(), curPath);
            } else {
                fileInfo.put(file.getName(), curPath);
            }
        }
    }


    public String getFilePathInRepo(String fileName) {
        if (fileInfo.containsKey(fileName)) {
            return fileInfo.get(fileName);
        }
        return null;
    }


    public String getFilePath(String fileName) {
        if (fileInfo.containsKey(fileName)) {
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