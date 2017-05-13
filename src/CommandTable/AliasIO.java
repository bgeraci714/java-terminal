package CommandTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AliasIO {
    final static String ALIAS_FILE_PATH = "src@alias_profile@alias_profile.txt".replace("@",File.separator);
    final static String SPACER = "k4LjQH-zE#:T7^kE";
    
    
    public static boolean saveAliases(HashMap<String, File> aliases){
        File aliasFile = new File(ALIAS_FILE_PATH);
        
        try {
            aliasFile.createNewFile(); // if file already exists will do nothing 
            
            PrintWriter writer = new PrintWriter(ALIAS_FILE_PATH, "UTF-8");
            
            for (Map.Entry<String, File> currEntry : aliases.entrySet()){
                writer.println(currEntry.getKey() + "," + currEntry.getValue().getAbsolutePath().replace(" ", "\\ "));
            }
                
            writer.close();
            return true;
        } 
        catch(IOException e){
            
            return false;
        }
        
        
        
        
    }
    
    public static HashMap<String,File> loadAliases(){
        
        HashMap<String, File> aliases = new HashMap<>();
        FileReader fin;
        String aliasKey, aliasFileString;
        File aliasFilePath;
        try {
            fin = new FileReader(ALIAS_FILE_PATH);
            Scanner info = new Scanner(fin);
            info.useDelimiter("[,\\n]"); // delimiters are commas, line feeds
            while (info.hasNext())      
            {
                
                aliasKey = info.next();   
                aliasFileString = info.next().replace("\\ ", " ");
                
                
                aliasFilePath = new File (aliasFileString);
                
                if (aliasFilePath.exists()){
                    aliases.put(aliasKey, aliasFilePath);
                }
            }
            
        } catch (FileNotFoundException|NoSuchElementException ex) {
            System.out.println("\nThere was an issue loading the alias profile file.");
        }
        
        
        return aliases;
    }
}
