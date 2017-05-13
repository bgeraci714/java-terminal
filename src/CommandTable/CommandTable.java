package CommandTable;

import FileTree.FileTree;

import java.awt.Desktop;
import java.io.File; 
import java.io.IOException;
import java.net.MalformedURLException; 
import java.net.URL;
import java.util.HashMap; 
import java.util.LinkedList;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

public class CommandTable {
    
    protected File cwd;
    protected boolean stillTakingInput = true;
    Runnable ftree;
    protected Thread treeThread;
    protected HashMap<String, File> aliases;
    final static String[] VALID_COMMANDS = {"ls", "cd", "cwd", "open", "quit", 
                                            "tree", "find", "rm_alias", "alias"};
    
    public CommandTable(){
        cwd = new File(System.getProperty("user.dir"));
        FileTree filetree = new FileTree();
        filetree.setCwd(cwd);
        ftree = (Runnable) filetree;
        
        aliases = new HashMap<>();
        
        
        treeThread = new Thread(ftree);
        treeThread.start();
    }
    
    public CommandTable(File dir){
        cwd = dir;
        FileTree filetree = new FileTree();
        filetree.setCwd(cwd);
        ftree = (Runnable) filetree;
        
        aliases = new HashMap<>();
        
        treeThread = new Thread(ftree);
        treeThread.start();
    }
    
    public String execute(String command){
        
        String cmd = parseCmd(command);
        
        if (cmd == null)
            return "Invalid input.";
        
        
        String returnStatement = "";
        switch (cmd){
            case "quit":
                treeThread.interrupt();
                stillTakingInput = false;
                break;
            case "rm_alias":
                returnStatement = rm_alias(command);
                break;
            case "alias":
                returnStatement = alias(command);
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
            case "find":
                returnStatement = find(command);
                break;
            default: 
                returnStatement = "Invalid command.";
        }
        return returnStatement;
    }
    
    protected synchronized String tree(){
        
        // we join here because we explicityly want what the other thread is working on. 
        try {
            treeThread.join();
        } catch(InterruptedException ex){
            System.out.println("Thread might have been interrupted.");
        }
        
        FileTree fileTree = (FileTree) ftree;
        
        return fileTree.toString() + fileTree.getAnalysisString();
    }
    
    protected String open(String command){
        String returnStatement;
        String fileName = command.replace("open", "").trim();
        
        // attempts to open website if -w flag is present
        if (fileName.startsWith("-w") || fileName.endsWith("-w")){
            
            try{ 
                //URL url = new URL("http://www.google.com");
                String urlString = fileName.replace("-w", "").trim();
                
                // matches www, en, and any other 0-3 character starting sequences 
                // so long as there is a period at the end
                Pattern p = Pattern.compile("(\\w{0,3}\\.{1})");
                Matcher m = p.matcher(urlString);
                
                // appends http to the url if needed
                if (m.find() && m.start() == 0){
                    urlString = "http://" + urlString;
                }
                
                // uses the android matcher to make sure the url is valid 
                if (Patterns.WEB_URL.matcher(urlString).matches()){
                    WebHandler.openWebpage(new URL(urlString));
                }
                else 
                    throw new MalformedURLException();
                
                returnStatement = "Opening webpage now...";
                
            } catch (MalformedURLException ex) {
                returnStatement =  "There was a problem with the URL.\n"
                        + "The proper format is prefix.website.[com/org/io/etc...]";
            }
            
            return returnStatement;
        }
        
        File file;
        
        // checks to see if it's an alias
        if (aliases.containsKey(fileName)) 
            file = aliases.get(fileName);
        
        // otherwise tries to open file
        else 
        {          
            fileName = cwd.getAbsolutePath() + File.separator + fileName;
            file = new File(fileName);
        }
        
        
        // verifies that the file actually is a file 
        if (file.exists() && file.isFile())
            try {
                Desktop.getDesktop().open(file);
                returnStatement = "Opening file now...";
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
        
        if (aliases.containsKey(trimmed) && aliases.get(trimmed).isDirectory()){
            cwd = aliases.get(trimmed);
        }
        
        // just going one level up
        else if (command.contains("..")){
            cwd = cwd.getParentFile();
        }
        
        // going all the way to the top
        else if (trimmed.length() == 0){
            
            while(cwd.getParentFile() != null) {
                cwd = cwd.getParentFile();
            }
        } 
        // move to a specific folder (down)
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
            
            treeThread.interrupt();
            
            ((FileTree) ftree).setCwd(cwd);
            
            treeThread = new Thread((Runnable) ftree);
            treeThread.start();
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
                        text += Colors.getTypeColor(file) + file + ANSI_RESET + " ";
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
      
    private String find(String command) 
    {
        String fileName = command.replace("find", "").trim();
        LinkedList<File> currentFiles = new LinkedList<>();
        currentFiles.addAll(recFileList(cwd));
        
        StringBuilder sb = new StringBuilder();
        //Prune the bad results
        for (File fileI : currentFiles) 
        {
            if (fileI.getName().toLowerCase().contains(fileName.toLowerCase())) 
            {
                String FP = fileI.getAbsolutePath();
                sb.append(Colors.highlight(FP, fileName));
                sb.append(System.getProperty("line.separator"));
            }
        }
        
        if (sb.toString().equals(""))
            return "No results found.";
        else 
            return sb.toString();
        
    }
    
    private LinkedList<File> recFileList(File folder)
    {
        LinkedList<File> recList = new LinkedList<>();
        
        for (File workingFile : folder.listFiles()) 
        {
            if (workingFile.isDirectory()) 
            {
                recList.addAll(recFileList(workingFile));
                recList.add(workingFile);
                
            }
            else
            {
                recList.add(workingFile);
            }
        }
        
        return recList;
    }

    private String alias(String command) {
        final String SPACER = "k4LjQH-zE#:T7^kE";
        String[] commandArguments = command.replace("alias", "").trim().replace("\\ ", SPACER).split(" ");
        
        if (commandArguments.length > 2 || 
            commandArguments.length == 0 || 
            commandArguments[0].length() == 0)
            return "Invalid number of arguments. Usage: alias alias_name [full file path]";
        
        
        if (commandArguments.length == 1){
            String alias = commandArguments[0].replace(SPACER, " ");
            aliases.put(alias, cwd);
            return "Saved " + alias + " as an alias for " + cwd.getAbsolutePath();
        }
        else {
            File aliasFilePath = null;
            String alias;
            int indexOfFile = 0;
            
            commandArguments[0] = commandArguments[0].replace(SPACER, " ");
            commandArguments[1] = commandArguments[1].replace(SPACER, " ");
            
            for (int i = 0; i < commandArguments.length; i++){
                
                if ((new File(commandArguments[i])).exists()){
                    aliasFilePath = new File(commandArguments[i]);
                    indexOfFile = i;
                    i++;
                }
            }

            if (aliasFilePath == null)
                return "Two arguments were given but no valid file path supplied.";
            
            if (indexOfFile == 0)
                alias = commandArguments[1];
            else 
                alias = commandArguments[0];
            
            aliases.put(alias, aliasFilePath);
            return "Saved " + commandArguments[0] + " as an alias for " + aliasFilePath.getAbsolutePath();
        }
    }
    
    private String rm_alias(String command) 
    {
        String key = command.replace("rm_alias", "").trim();
        
        if (aliases.containsKey(key)) 
        {
            aliases.remove(key);
            return key + " was successfully removed from your aliases.";
        }
        else
            return key + " was not a registered alias.";
    }
    
    
    // Simple and uninteresting getters/setters
    
    public void setAliases(HashMap<String, File> aliases){
        this.aliases = aliases;
    }
    
    public HashMap<String, File> getAliases(){
        return this.aliases;
    }
    
    public boolean isStillTakingInput(){
        return stillTakingInput;
    }
    
}
