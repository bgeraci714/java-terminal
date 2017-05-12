/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FileIO;

import java.io.File;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eric
 */
public class HashIO 
{
    private final String hashPath;
    public HashIO(String hashPath)
    {
        this.hashPath = hashPath;
        
        File hashFile = new File(this.hashPath);
        
        if(!hashFile.exists())
        {
            try 
            {
                hashFile.createNewFile();
            } catch (IOException ex) {}
            
            //System.out.print("alias_profile.txt created at ");
            //System.out.print(this.hashPath + "\n");
        }
        
    }
    
    public List<String> LoadHash()
    {
        List<String> hashes = new ArrayList<>();
        try
        {
            FileInputStream fStream = new FileInputStream(hashPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(fStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {//While stuff exists...
                
                // We're going to call alias() through the command table to properly parse out aliases 
                // in case a user changed the .txt file (just as a user could change a ./bash_profile
                sb.append("alias ");
                sb.append(line);
                hashes.add(sb.toString());
                sb.delete(0, sb.toString().length());
                
            }
            
            return hashes;            
        } catch(Exception ex){
            return hashes;
        }
    }
    
        public void SaveEvents(List<String> hashList)
    {

        Writer writer;
        Iterator itr = hashList.iterator();
        
        try
        {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.hashPath), "utf-8"));
            while (itr.hasNext())
            {
                writer.write((String)itr.next() + System.lineSeparator());
            }
            writer.close();
        }
        catch(IOException ex) {
            System.out.println("\nFailed to save aliases");
        }
    }
        
    
}
