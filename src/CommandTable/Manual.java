package CommandTable;

public class Manual {
    private static final String man = "This is a general manual for the Java Terminal.";
    final static String spacer = "             ";
    final static String[] VALID_COMMANDS = {"ls", "cd", "open", "quit", "grep", 
                                            "tree", "find", "rm_alias", "alias", "manual", "cwd"};  
    final static String[] COMMAND_DESCRIPTIONS = {"lists the files in the current directory", 
        
                                                  "changes the directory. \nUsage: "
                                                   + "\n" + spacer + "none -> goes to the root"
                                                   + "\n" + spacer + "full valid file path -> moves to said file path"
                                                   + "\n" + spacer + "file name/path below the current level -> moves to said file path"
                                                   + "\n" + spacer + "alias -> moves straight to that alias if it's a valid directory", 
                                                  
                                                  "opens a file or website passed in as an argument. \nUsage: "
                                                   + "\n" + spacer + "valid/file/path -> opens file with default program. or alias can be used as well, takes into account current working directory"
                                                   + "\n" + spacer + "alias -> opens file with default program."
                                                   + "\n" + spacer + "-w prefix.website.[com/org/io/etc...] -> opens website", 
                                                  
                                                  "exits the terminal and saves aliases", 
                                                  
                                                  "searches within a textfile for a given search query. \nUsage: "
                                                  + "\n" + spacer + "\"query\" full_file_path", 
                                                  
                                                  "pretty prints out the below file structure in a tree-like format", 
                                                  
                                                  "searches all directory/file names below the current working directory for a search query. \nUsage: "
                                                  + "\n" + spacer + "search_query -> returns highlighted occurences of the search query", 
                                                  
                                                  "removes an alias passed as an argument. \nUsage: "
                                                  + "\n" + spacer + "alias_to_remove -> removes said alias", 
                                                  
                                                  "creates a shortcut to a given file or directory path. Makes traversing the file structure much easier. \n"
                                                  + spacer + "Loads and saves a user's aliases at the start and end of a terminal session. \nUsage:"
                                                  + "\n" + spacer + "alias_name -> saves an alias for the current working directory"
                                                  + "\n" + spacer + "alias_name full_file_path -> saves an alias for the specified file path", 
                                                  
                                                  
                                                  
                                                  "returns the manual",
    
                                                  "prints out the current working directory"};  
    
    public static String general(){
        String spacer = "             ";
        String result = "This project is meant to mimic the Unix Terminal in a handful of its functions.\n" +
                "Valid Commands: \n";
        
        for (int i = 0; i < VALID_COMMANDS.length; i++){
            result += "Command:     " + VALID_COMMANDS[i] + "\nDescription: " + COMMAND_DESCRIPTIONS[i] + "\n\n";
        }
        
        return result;
    }
           
}
