package programs;

import com.jcraft.jsch.IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import static programs.BlameInspector.getName;


public class Folder {
    ArrayList<Folder>folders;
    ArrayList<String>files;
    Integer indent;
    String path;
    String folderName;

    public Folder(String path, Integer number) throws IOException, InterruptedException {
        folders = new ArrayList<>();
        folderName = getName(path);
        this.path = path;
        files = out_bash("ls ", path);
        indent = number;
        Iterator<String>iterator = files.iterator();
        while (iterator.hasNext()){
            String filename = iterator.next();
            try {
                out_bash("cd ", path + '/' + filename);
            }catch (Exception ex){
                continue;
            }
            folders.add(new Folder(path + '/' + filename, this.indent + 1));
        }
    }


    public String getFolderName(){
        return folderName;
    }

    public void showFiles(){
        Iterator<String>iterator = files.iterator();
        while (iterator.hasNext()){
            String filename = iterator.next();
            for(int i = 0; i < indent; i++){
                System.out.print('\t');
            }
            if(indent != 0){
                System.out.print('-');
            }
            System.out.print(filename);
            if(!folders.isEmpty()){
                if(checkFolderName(filename)){
                    System.out.print(":" + "\n");
                    getFolder(filename).showFiles();
                    continue;
                }
            }
            System.out.println();
        }
    }


    public Folder getFolder(String foldername){
        Iterator<Folder> iterator = folders.iterator();
        while (iterator.hasNext()){
            Folder folder = iterator.next();
            if(folder.getFolderName().equals(foldername)){
                return folder;
            }
        }
        return null;
    }

    public boolean checkFolderName(String foldername){
        Iterator<Folder> iterator = folders.iterator();
        while (iterator.hasNext()){
            Folder folder = iterator.next();
            if(folder.getFolderName().equals(foldername)){
                return true;
            }
        }
        return false;
    }


    public Object findFile(String file){
        Iterator<String>iterator = files.iterator();
        String cur_path = this.path;
        while (iterator.hasNext()){
            String filename = iterator.next();
            if(filename.equals(file)){
                return this.path;
            }
        }
        if(!folders.isEmpty()){
            Iterator<Folder>folderIterator = folders.iterator();
            while (folderIterator.hasNext()){
                Object obj = folderIterator.next().findFile(file);
                if(obj != null){
                    return obj;
                }
            }
        }
        return null;
    }


    public static ArrayList<String> out_bash(String command, String dir) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(dir));
        processBuilder.command("bash", "-c", command);
        StringBuilder output = new StringBuilder();
        ArrayList<String>files = new ArrayList<>();
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                files.add(line);
            }
            process.waitFor();
        } catch (IOException e) {
            throw e;
        } catch (InterruptedException e) {
            throw e;
        }
        return files;
    }
}
