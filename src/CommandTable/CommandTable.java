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
import java.util.Stack;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

public class CommandTable {
    
    protected boolean willPrintTime = false; 
    protected File cwd;
    protected boolean stillTakingInput = true;
    Runnable ftree;
    protected Thread treeThread;
    protected HashMap<String, File> aliases;
    final static String[] VALID_COMMANDS = {"ls", "cd", "cwd", "open", "quit", "timer", "grep", 
                                            "tree", "find", "rm_alias", "alias", "manual",
                                            "quickTree"};
    
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
        long startTime = 0, endTime = 0;
        
        
        String cmd = parseCmd(command);
        
        if (cmd == null)
            return "Invalid input.";
        
        
        String returnStatement = "";
        startTime = System.nanoTime();
        switch (cmd){
            case "timer":
                willPrintTime = !willPrintTime;
                break;
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
            case "quickTree":
                returnStatement = TreeFiddy();
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
                break;
        }
        endTime = System.nanoTime();
        Double timeDiff = (endTime - startTime) / 1000000000.0;
        if (willPrintTime){
            returnStatement = "Time taken: " + timeDiff.floatValue() + " seconds.\n" + returnStatement;
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
        return Manual.specMethod(command);
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
        String blah = command.replace("grep", "").trim();
        String searchTerm = "";
       
        //Grab the section between quotes
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(blah);
       
        String path = "";
        List<String> fullWords = new ArrayList<>();
       
        int matchCount = 0;
            while (m.find())
            {
                searchTerm = m.group();
                path = blah.replace(searchTerm,"").trim();
                //Remove the quotes from the string
                searchTerm = searchTerm.substring(1, searchTerm.length() -1);
                matchCount ++;
            }
           
        //File validation
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
            while((line = br.readLine()) != null) //While there are lines to read
            {
                if (line.contains(searchTerm)) //if the line contains the term
                {
                    String lineRemain = line; //push full line into the function
                    while (lineRemain.contains(searchTerm))
                    {
                        int[] section = wordIndexPull(lineRemain, searchTerm);
                        //Returns the indexes of where the first occurance of that word begins and ends
                        fullWords.add(lineRemain.substring(section[0], section[1])); //So add that word into the list.
                        lineRemain = lineRemain.substring(section[1]); //reduce the search of the line, repeat.
                    }
                }
            }
                       
        } catch (Exception ex){}
       
        //Build the string
       
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
   
   
    //Warning, this requires you to be sure the search term is contained in the line.
    private int[] wordIndexPull(String line, String searchTerm)
    {
            int start = line.indexOf(searchTerm);
            //Index of the beginning of the term
            boolean found = false;
            int secStart = start;
            int secEnd = start + searchTerm.length();
            //index of the end of the term
            while (secStart > 0 && !found)
            {
                //Go to the left of the string until the term is seperated
                if (line.toCharArray()[secStart] == ' ')
                {
                    found = true;
                }
                else
                    secStart --;
                //index of the full word's start.
            }
            found = false;
            while (secEnd < line.length() && !found)
            {
                //Go to the right of the string until the term is seperated
                if(line.toCharArray()[secEnd] == ' ')
                {
                    found = true;
                }
                else
                    secEnd ++;
                //secEnd is now the index of the final section.
            }
           
        int[] section = new int[] {secStart, secEnd};
        return section;
    }
    
    public String TreeFiddy()
    // wrote this for the heck of it. Take a lot of what the FileTree structure does but work directly with
    // with the files themselves to speed up processing. Thus far, it's proven to be much quicker. 
    // because there's a lot less overhead, each file is only ever needed once. 
    {
        String result = "";
        
        int numFiles = 0;
        int numDirs = 0;
        
        Stack<File> nodeStack = new Stack<>();
        File currNode;
        nodeStack.push(cwd);
        
        
        while (!nodeStack.isEmpty()) {
            
            currNode = nodeStack.pop();
            if (currNode != null){

                result += "\n" + parentCountNonRec(currNode) + currNode.getName();
            }
            
            if (currNode != null && currNode.listFiles() != null){
                numDirs++;
                for (File childFile : currNode.listFiles()){
                    if (!currNode.getName().startsWith("."))
                        nodeStack.push(childFile);
                }
            }
            else 
                numFiles++;
            // otherwise the node is null, don't do anything with it
        
        }
        String analysis = "\n\n" + numDirs;

        if (numDirs > 1)
            analysis += " directories, ";
        else
            analysis += " directory, ";

        analysis += numFiles;

        if (numFiles > 1)
            analysis += " files";
        else
            analysis += " file";

        
        return result + analysis;
    }
    
    private String parentCountNonRec(File child){
        String result = "";
        File currFile = child;
        while (currFile.getParentFile() != null){
            currFile = currFile.getParentFile();
            result += "---";
        }
        return result;
    }
    
    private String parentCount(File child){
        if (child.getParentFile() == null)
            return "";
        else 
            return "---" + parentCount(child.getParentFile());
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
