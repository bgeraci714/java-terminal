package Main;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

/*
Current TODOS
 - find (make a tree of file structure below, and above (if asked for)
 - tree
 - grep 
 - turn the switch statement into a hashtable
 - improve the parser 
 - add more functions for funsies
 - flags (ls -x, rearrange things/output, *.py => only .py files, maybe also something that would show 
    when the files were created, size, last opened, or what have you)
*/


public class Main {
    
    final static String[] VALID_COMMANDS = {"ls", "cd", "cwd", "open", "quit"};
    public static void main(String[] args) throws IOException{
        
        
        Scanner sc = new Scanner(System.in);
        boolean stillTakingInput = true;
        File cwd = new File(System.getProperty("user.dir"));
        System.out.println("Working Directory = " +
              System.getProperty("user.dir")+ "\n");
        while(stillTakingInput){
            
            System.out.print(">> ");
            String command = sc.nextLine();
            String cmd = parseCmd(command);
            if (cmd == null){
                System.out.println("Invalid input.");
                continue;
            }
            
            // current issues: 
            // can't move to bgspiral (not sure why, /Users/)
            // get a null pointer at Library (/Library)
            switch (cmd){
                case "quit":
                    stillTakingInput = false;
                    break;
                case "ls":
                    for (String file : cwd.list())
                        if (!file.startsWith("."))
                            System.out.print(getTypeColor(file) + file + ANSI_BLACK + " ");
                    System.out.println();
                    break;
                case "cd":
                    boolean validFolder = true;
                    String trimmed = command.replace("cd", "").trim();
                    if (command.contains("..")){
                        cwd = cwd.getParentFile();
                    }
                    
                    else if (trimmed.length() == 0)
                        while(cwd.getParentFile() != null){
                            cwd = cwd.getParentFile();
                        }
                    else {//((new File(cwd.getAbsolutePath() + trimmed)).isDirectory()) {
                        
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
                        System.out.println(cwd.getAbsolutePath());
                    }
                    else 
                        System.out.println("Invalid folder location.");
                    break;
                case "cwd":
                    System.out.println(cwd.getAbsolutePath());
                    break;
                case "open":
                    String fileName = cwd.getAbsolutePath() + File.separator + command.replace("open", "").trim();
                    File file = new File(fileName);
                    
                    if (file.isFile())
                        Desktop.getDesktop().open(file);
                    else 
                        System.out.println("Invalid file location.");
                    break;
                default: 
                    System.out.println("Invalid command.");
                   
            }
            
        }
        
        
        
        
        //File cwd = new File(System.getProperty("user.dir"));
        /*
        File parent = cwd.getParentFile();
        for(String entry : cwd.list())
            System.out.print(entry + " ");
        System.out.println();
        for (String entry : parent.list())
            System.out.print(entry + " ");
        System.out.println();
        */
    }
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    public static String getTypeColor(String fileName){
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
    
    public static String parseCmd(String cmd){
        String[] truncated = cmd.trim().split(" ");
        for (String command : VALID_COMMANDS){
            if (truncated[0].contains(command)){
                return command;
            }
        }
        return null;
    }
    
    
}
