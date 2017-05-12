package Main;

import CommandTable.CommandTable;
import java.io.IOException;
import java.util.Scanner;

/*
Current TODOS
 - find (make a tree of file structure below, and above (if asked for)
 - grep 
 - flags (ls -x, rearrange things/output, *.py => only .py files, maybe also something that would show 
    when the files were created, size, last opened, or what have you)
*/


public class Main {
    
    
    
    public static void main(String[] args) throws IOException{
        
        CommandTable commandLine = new CommandTable();
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Working Directory = " +
              System.getProperty("user.dir")+ "\n");
        
        while(commandLine.isStillTakingInput()){
            
            System.out.print(">> ");
            String command = sc.nextLine();
            
            System.out.println(commandLine.execute(command));
            //System.out.println(java.lang.Thread.activeCount());
            
        }
        
    }
    
    
    
    
    
    
}
