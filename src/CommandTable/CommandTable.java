package CommandTable;

import FileTree.FileTree;
import Queue.LinkedQueue;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class CommandTable {
    
    File cwd;
    boolean stillTakingInput = true;
    final static String[] VALID_COMMANDS = {"ls", "cd", "cwd", "open", "quit", "tree"};
    
    public CommandTable(){
        cwd = new File(System.getProperty("user.dir"));
    }
    
    public CommandTable(File dir){
        cwd = dir;
    }
    
    public boolean isStillTakingInput(){
        return stillTakingInput;
    }
    
    public String execute(String command){
        String cmd = parseCmd(command);
        
        if (cmd == null)
            return "Invalid input.";
        
        
        String returnStatement = "";
        switch (cmd){
            case "quit":
                stillTakingInput = false;
                break;
            case "tree":
                returnStatement = tree();
                break;
            case "ls":
                returnStatement = ls();
                break;
            case "cd":
                returnStatement = cd(command);
                break;
            case "cwd":
                returnStatement = cwd.getAbsolutePath();
                break;
            case "open":
                returnStatement = open(command);
                break;
            default: 
                returnStatement = "Invalid command.";
        }
        return returnStatement;
    }
    
    protected String tree(){
        String cwdString = cwd.getAbsolutePath();
        String rootDir = cwd.getName();
        FileTree<String> ftree = new FileTree<>();
        
        
        LinkedQueue<File> fileQueue = new LinkedQueue<>();
        File currFile;
        fileQueue.enqueue(cwd);
        
        System.out.println(ftree.toString());
        while(!fileQueue.isEmpty()){
            currFile = fileQueue.dequeue();
            if (currFile != null && !currFile.getName().startsWith(".")){
                ftree.add(currFile.getParentFile().getName(), currFile.getName());
                if (currFile.isDirectory())
                    for (File childFile : currFile.listFiles())
                        fileQueue.enqueue(childFile);
                
                
            }
        }
        return ftree.toString();
    }
    
    
    protected String open(String command){
        String returnStatement = "";
        String fileName = cwd.getAbsolutePath() + File.separator + command.replace("open", "").trim();
        File file = new File(fileName);

        if (file.isFile())
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                returnStatement = ex.toString();
            }
        else 
            returnStatement = "Invalid file location.";
        
        return returnStatement;
    }
    
    protected String cd(String command){
        boolean validFolder = true;
        String returnStatement;
        String trimmed = command.replace("cd", "").trim();
        if (command.contains("..")){
            cwd = cwd.getParentFile();
        }

        else if (trimmed.length() == 0){
            
            while(cwd.getParentFile() != null) {
                cwd = cwd.getParentFile();
            }
        }    
        else {

            if (cwd.getAbsolutePath().endsWith(File.separator)){
                if ((new File(cwd.getAbsolutePath() + trimmed)).isDirectory())
                    cwd = new File(cwd.getAbsolutePath() + trimmed);
                else 
                    validFolder = false;
            } 
            else {
                if ((new File(cwd.getAbsolutePath() + File.separator + trimmed)).isDirectory())
                    cwd = new File(cwd.getAbsolutePath() + File.separator + trimmed );
                else 
                    validFolder = false;
            }
        }
        if (validFolder){
            returnStatement = cwd.getAbsolutePath();
        }
        else 
            returnStatement = "Invalid folder location.";
        
        return returnStatement;
    }
    
    protected String ls (){
        final String ANSI_RESET = "\u001B[0m";
        String text = "";
        for (String file : cwd.list())
                    if (!file.startsWith("."))
                        text += getTypeColor(file) + file + ANSI_RESET + " ";
        return text;
    }
    
    private String parseCmd(String cmd){
        String[] truncated = cmd.trim().split(" ");
        for (String command : VALID_COMMANDS){
            if (truncated[0].contains(command)){
                return command;
            }
        }
        return null;
    }
      
    private String getTypeColor(String fileName){
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_WHITE = "\u001B[37m";
        
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1)
            return ANSI_BLACK;
        
        String type = fileName.substring(dotIndex);
        
        switch(type){
            case ".jpg":
            case ".png":
                return ANSI_YELLOW;
            case ".doc":
            case ".docx":
            case ".txt":
            case ".rtf":
                return ANSI_BLUE;
            case ".pdf":
                return ANSI_RED;
            case ".xls":
            case ".xlsx":
                return ANSI_GREEN;
            default:
                return ANSI_BLACK;
        }        
   
       
    }
}
