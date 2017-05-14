package CommandTable;

public class Manual {
    private static final String man = "This is a general manual for the Java Terminal.";
    final static String spacer = "             ";
    final static String[] VALID_COMMANDS = {"ls", "cd", "open", "quit", "grep", 
                                            "tree", "find", "rm_alias", "alias", "manual", "cwd"};  
    final static String[] COMMAND_DESCRIPTIONS = {
                                                  "lists the files in the current directory\nUsage:       "
                                                   + "ls", 
        
                                                  "changes the directory. \nUsage:       "
                                                   + "cd .. -> Moves you up one level. Most advised way to move through the directory structure." 
                                                   + "\n" + spacer + "cd -> goes to the root directory, not advised to call tree here."
                                                   + "\n" + spacer + "cd full/valid/file/path -> moves to said file path"
                                                   + "\n" + spacer + "cd name_of_an_alias -> moves straight to that alias if it's a valid directory", 
                                                  
                                                  "opens a file or website passed in as an argument. takes into account current working directory\nUsage:       "
                                                   + "open valid/file/path -> opens file with default program. "
                                                   + "\n" + spacer + "open name_of_an_alias -> opens file with default program."
                                                   + "\n" + spacer + "open -w prefix.website.[com/org/io/etc...] -> opens website", 
                                                  
                                                  "exits the terminal and saves aliases. \nUsage:\n"
                                                   + spacer + "quit ", 
                                                  
                                                  "searches within a textfile for a given search query. \nUsage:       "
                                                   + "grep \"query\" full_file_path", 
                                                  
                                                  "pretty prints out the below file structure in a tree-like format. \n"
                                                  + spacer + "advised not to use at the root dir or other folders with a lot (11,000+) folders/files. \n"
                                                  + spacer + "do note, that the project is still working. It just takes time to complete!", 
                                                  
                                                  "searches all directory/file names below the current working directory for a search query. \nUsage:       "
                                                  + "find search_query -> returns highlighted occurences of the search query", 
                                                  
                                                  "removes an alias passed as an argument. \nUsage:       "
                                                  + "rm_alias alias_to_remove -> removes said alias", 
                                                  
                                                  "creates a shortcut to a given file or directory path. Makes traversing the file structure much easier. \n"
                                                  + spacer + "Loads and saves a user's aliases at the start and end of a terminal session. \nUsage:       "
                                                  + "alias name_of_an_alias -> saves an alias for the current working directory"
                                                  + "\n" + spacer + "alias name_of_an_alias full/file/path -> saves an alias for the specified file path", 
                                                 
                                                  "returns the manual\nUsage:       "
                                                   + "manual ",
    
                                                  "prints out the current working directory\nUsage:       "
                                                   + "cwd"};  
    
    public static String general(){
        String spacer = "             ";
        String result = "This project is meant to mimic the Unix Terminal in a handful of its functions.\n" +
                "Valid Commands: \n";
        
        for (int i = 0; i < VALID_COMMANDS.length; i++){
            result += "Command:     " + Colors.highlight(VALID_COMMANDS[i], VALID_COMMANDS[i], "green") + "\nDescription: " + COMMAND_DESCRIPTIONS[i] + "\n\n";
            //result = Colors.highlight(result, VALID_COMMANDS[i], "green");
        }
        
        return result;
    }
           
}
