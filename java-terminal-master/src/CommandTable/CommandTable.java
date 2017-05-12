package CommandTable;

import FileIO.HashIO;
import FileTree.FileTree;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandTable {
    
    
    protected HashIO hIO;
    protected File hashCache;
    protected List<String> aliasList = new  ArrayList<>();
    protected File cwd;
    protected boolean stillTakingInput = true;
    Runnable ftree;
    protected Thread treeThread;
    protected HashMap<String, File> aliases;
    final static String[] VALID_COMMANDS = {"ls", "cd", "cwd", "open", "quit", 
                                            "tree", "find", "alias"};
    
    public CommandTable(){
        File dir = new File(System.getProperty("user.dir"));
        construct(dir);
    }
    
    public CommandTable(File dir)
    {
        construct(dir);
    }
    
    private void construct(File dir)
    {
        List<String> loadList = null;
        cwd = dir;
        FileTree filetree = new FileTree();
        filetree.setCwd(cwd);
        
        hashCache = new File("Alias.txt");
        hIO = new HashIO(hashCache.getAbsolutePath()); 
        
            //hIO equals new HashIO
            //HashCache dot get AbsolutePath
            //Say that five times fast.
            
        
        try {loadList = hIO.LoadHash();} 
        catch (Exception ex) {Logger.getLogger(CommandTable.class.getName()).log(Level.SEVERE, null, ex);}
        
        aliases = new HashMap<>(); 
        Iterator itr = loadList.iterator();
        while (itr.hasNext())
        {
            alias((String)itr.next());
        }
        
        ftree = (Runnable) filetree;
        treeThread = new Thread(ftree);
        treeThread.start();
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
                treeThread.interrupt();
                stillTakingInput = false;
                break;
            case "alias":
                returnStatement = alias(command);
                {
                    try {hIO.SaveEvents(aliasList);} 
                    catch (Exception ex) 
                    {Logger.getLogger(CommandTable.class.getName()).log(Level.SEVERE, null, ex);}
                }
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
        
        String analysis = "\n\n" + fileTree.getNumDirs() + " directories, "
                + fileTree.getNumFiles() + " files";
        
        return fileTree.toString() + analysis;
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
                    openWebpage(new URL(urlString));
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
        if (aliases.containsKey(fileName)) // checks to see if it's an alias
            file = aliases.get(fileName);
        else // otherwise tries to open file
        {          
            fileName = cwd.getAbsolutePath() + File.separator + fileName;
            file = new File(fileName);
        }
        
        

        if (file.isFile())
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
            
            FileTree ftree = (FileTree) this.ftree;
            ftree.setCwd(cwd);
            
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
                        text += getTypeColor(file) + file + ANSI_RESET + " ";
        return text;
    }
    
    protected String highlight(String full, String block)
    {
        return highlight(full,block,"Purple");
    }
    
    protected String highlight(String full, String block, String color)
    {       
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_BLACK = "\u001B[30m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_PURPLE = "\u001B[35m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_WHITE = "\u001B[37m";
        
        switch(color.toUpperCase())
        {
            case "BLACK":
                color = ANSI_BLACK;
                break;
            case "RED":
                color = ANSI_RED;
                break;
            case "GREEN":
                color = ANSI_GREEN;
                break;
            case "YELLOW":
                color = ANSI_YELLOW;
                break;
            case "BLUE":
                color = ANSI_BLUE;
                break;
            case "PURPLE":
                color = ANSI_PURPLE;
                break;
            case "CYAN":
                color = ANSI_CYAN;
                break;
            case "WHITE":
                color = ANSI_WHITE;
                break;
            default:
                color = ANSI_YELLOW;
                break;                
        }
        
        StringBuilder sb = new StringBuilder();
        int start = full.toLowerCase().indexOf(block.toLowerCase());
        
        sb.append(full, 0, start);
        sb.append(color);
        sb.append(full, start, start + block.length());
        sb.append(ANSI_RESET);
        sb.append(full, start + block.length(), full.length());
        
        return sb.toString();
                
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
    
    // used curteously from http://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
    private void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                System.out.println("there was a problem");
            }
        }
    }

    private void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            System.out.println("there was a problem");
        }
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
                sb.append(highlight(FP, fileName));
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
        //Ran into issues with seperation with " " characters in file paths.
        //replacing it with a constant string of characters.
        final String SPACER = "k4LjQH-zE#:T7^kE";
        StringBuilder sb = new StringBuilder();
        
        
        String[] commandArguments = command.replace("alias", "").trim().replace("\\ ", SPACER).split(" ");
        
        if (commandArguments.length > 2 || 
            commandArguments.length == 0 || 
            commandArguments[0].length() == 0)
            return "Invalid number of arguments. Usage: alias alias_name [full file path]";
        
        
        if (commandArguments.length == 1){
            String alias = commandArguments[0].replace(SPACER, " ");
            aliases.put(alias, cwd);
            sb.append(alias);
            sb.append(" ");
            sb.append(cwd);
            aliasList.add(sb.toString());
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
            sb.append(alias);
            sb.append(" ");
            sb.append(aliasFilePath.getAbsolutePath());
            aliasList.add(sb.toString());
            return "Saved " + commandArguments[0] + " as an alias for " + aliasFilePath.getAbsolutePath();
        }
    }
}
