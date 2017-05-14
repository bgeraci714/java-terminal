package CommandTable;

import FileTree.FileTree;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File; 
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException; 
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

public class CommandTable {
    
    protected File cwd;
    protected boolean stillTakingInput = true;
    Runnable ftree;
    protected Thread treeThread;
    protected HashMap<String, File> aliases;
    final static String[] VALID_COMMANDS = {"ls", "cd", "cwd", "open", "quit", "grep", 
                                            "tree", "find", "rm_alias", "alias", "manual"};
    
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
            case "manual":
                returnStatement = manual(command);
                break;
            case "alias":
                returnStatement = alias(command);
                break;
            case "tree":
                returnStatement = tree();
                break;
            case "grep":
                returnStatement = grep(command);
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
        String fileName = command.replaceFirst("open", "").trim();
        
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
        String trimmed = command.replaceFirst("cd", "").trim();
        
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
        String fileName = command.replaceFirst("find", "").trim();
        LinkedList<File> currentFiles = new LinkedList<>();
        currentFiles.addAll(recFileFind(cwd, fileName));
        
        StringBuilder sb = new StringBuilder();

        for (File fileI : currentFiles) 
        {
            String FP = fileI.getAbsolutePath();
            sb.append(Colors.highlight(FP, fileName));
            sb.append(System.getProperty("line.separator"));
        }
        
        if (sb.toString().equals(""))
            return "No results found.";
        else 
            return sb.toString();
        
    }
    
    private LinkedList<File> recFileFind(File folder, String contains)
    {
        LinkedList<File> recList = new LinkedList<>();
        
        for (File workingFile : folder.listFiles()) 
        {
            if (workingFile.isDirectory()) 
            {
                recList.addAll(recFileFind(workingFile, contains));
                if (workingFile.getName().toLowerCase().contains(contains.toLowerCase())) 
                {
                    recList.add(workingFile);
                }
            }
            else
            {
                if (workingFile.getName().toLowerCase().contains(contains.toLowerCase())) 
                {
                    recList.add(workingFile);
                }
            }
        }
        
        return recList;
    }
    
    private String manual(String command){
        return Manual.general();
    }

    private String alias(String command) {
        final String SPACER = "k4LjQH-zE#:T7^kE";
        String[] commandArguments = command.replaceFirst("alias", "").trim().replace("\\ ", SPACER).split(" ");
        
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
            System.out.println(commandArguments[1]);
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
        String key = command.replaceFirst("rm_alias", "").trim();
        
        if (aliases.containsKey(key)) 
        {
            aliases.remove(key);
            return key + " was successfully removed from your aliases.";
        }
        else
            return key + " was not a registered alias.";
    }
    
    private String grep(String command) 
    {
        String blah = command.replaceFirst("grep", "").trim();
        String searchTerm = "";
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(blah);
        String path = "";
        List<String> fullWords = new ArrayList<>();
        
        int matchCount = 0;
            while (m.find()) 
            {
                searchTerm = m.group();
                path = blah.replace(searchTerm,"").trim();
                searchTerm = searchTerm.substring(1, searchTerm.length() -1);
                matchCount ++;
            }
            
        File fileToUse = new File(path);            
        if (matchCount != 1) 
            return "Please one and only one set of search terms";

        if (!fileToUse.exists()) 
            return "Supplied file does not exist";
        
        if (!fileToUse.canRead() || !fileToUse.isFile())
            return "Unable to read filetype";
        try
        {
            FileInputStream fStream = new FileInputStream(fileToUse);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            String line;
            while((line = br.readLine()) != null)
            {
                if (line.contains(searchTerm)) 
                {
                    String lineRemain = line;
                    while (lineRemain.contains(searchTerm))
                    {
                        int[] section = wordIndexPull(lineRemain, searchTerm);
                        fullWords.add(lineRemain.substring(section[0], section[1]));
                        lineRemain = lineRemain.substring(section[1]);
                    }
                }
            }
                        
        } catch (Exception ex){}
        
        StringBuilder sb = new StringBuilder();
        sb.append("Found: \"").append(searchTerm).append("\" ").append(fullWords.size());
        sb.append(" times in document:").append(System.lineSeparator());
        sb.append(path).append(System.lineSeparator());
        for (String word : fullWords) 
        {
            sb.append(word.trim());
            sb.append(" ");
        }
        return sb.toString();
    }
    
    private int[] wordIndexPull(String line, String searchTerm)
    {
        int start = line.indexOf(searchTerm);
            boolean found = false;
            int secStart = start;
            int secEnd = start + searchTerm.length();
            while (secStart > 0 && !found)
            {
                
                
                if (line.toCharArray()[secStart] == ' ')
                {
                    found = true;
                }
                else
                    secStart --;
            }
            found = false;
            while (secEnd < line.length() && !found)
            {

                if(line.toCharArray()[secEnd] == ' ')
                {
                    found = true;
                }
                else
                    secEnd ++;
            }
            
        int[] section = new int[] {secStart, secEnd};
        return section;
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
