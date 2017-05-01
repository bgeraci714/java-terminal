package Main;

import CommandTable.CommandTable;
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
    
    
    public static void main(String[] args) throws IOException{
        
        //File cwd = new File(System.getProperty("user.dir"));
        CommandTable commandLine = new CommandTable();
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Working Directory = " +
              System.getProperty("user.dir")+ "\n");
        
        while(commandLine.isStillTakingInput()){
            
            System.out.print(">> ");
            String command = sc.nextLine();
            
            System.out.println(commandLine.execute(command));           
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
    
    
    
    
    
    
}
