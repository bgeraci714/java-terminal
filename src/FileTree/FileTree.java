package FileTree;

import Queue.LinkedQueue;
import Stack.LinkedStack;
import java.io.File;
import java.util.Iterator;

public class FileTree extends Tree implements Runnable{
    protected int numDirs = 0;
    protected int numFiles = 0;
    protected File cwd;
    protected boolean finishedBuilding = false;
    
    @Override
    public void run() {
        
        buildTree();
    }
    
    public void setCwd(File cwd){
        this.cwd = cwd;
    }
    
    public synchronized void buildTree(){
        
        Node<File> lowerRoot = find(cwd);
        
        // makes sure the previous tree had finished so we don't get an incomplete tree
        if (finishedBuilding && lowerRoot != null){
            
            finishedBuilding = false;
            reRootDown(lowerRoot);
            
            if (!Thread.currentThread().isInterrupted())
                finishedBuilding = true;
            
            return;
        }
        
        finishedBuilding = false;
        root = null;
        numDirs = 0;
        numFiles = 0;
        LinkedQueue<File> fileQueue = new LinkedQueue<>();
        File currFile;
        fileQueue.enqueue(cwd);
        
        while(!fileQueue.isEmpty() && !Thread.currentThread().isInterrupted()){
            
            currFile = fileQueue.dequeue();

            if (currFile != null && !currFile.getName().startsWith(".")){
                add(currFile.getParentFile(), currFile);
                
                if (currFile.isDirectory()){
                    numDirs++;
                    try{
                        for (File childFile : currFile.listFiles()){
                            fileQueue.enqueue(childFile);
                        }
                    } catch(NullPointerException ex){} // don't do anything with the error
                    
                    
                }
                else 
                    numFiles++;
            }
        }
        
        if (!Thread.currentThread().isInterrupted()){
            finishedBuilding = true;
        }
    }
    
    public synchronized int getNumDirs(){
        return numDirs;
    }
    
    public synchronized int getNumFiles(){
        return numFiles;
    }
    
    @Override 
    public synchronized String toString(){
        String result = "";
        
        LinkedStack<Node<File>> nodeStack = new LinkedStack<>();
        Node<File> currNode;
        nodeStack.push(root);
        
        while (!nodeStack.isEmpty()) {
        
            currNode = nodeStack.top();
            nodeStack.pop();
            if (currNode != null){
                result += "\n";
                for(int i = 0; i < currNode.getLevel(); i++ )
                    result += "---";
                
                // important difference between FileTree and Tree is the use of 
                // .getName() which is specific to the File class 
                // this is necessary for making sure that when child files/folders 
                // are added of type File as opposed to of type string, they are 
                // placed where they should be as opposed to a folder with the same name
                // (all files with a parent of "src" were being placed in the same subtree for example)
                result += currNode.getData().getName();
            }
            if (currNode != null && !currNode.getChildren().isEmpty()){
                Iterator childNodes  = currNode.getChildren().iterator();
                while(childNodes.hasNext()){
                    nodeStack.push((Node) childNodes.next());
                }
            }
            // otherwise the node is null, don't do anything with it
        }
        
        return result;
    }
    
    public synchronized String getAnalysisString(){
        
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
        
        
        return analysis;
    
    }
    
    // removed the find call because we need it anyways before calling reRootDown
    public boolean reRootDown(Node<File> newRoot){
        if (newRoot != null){
            int levelDiff = root.getLevel() - newRoot.getLevel();
            root = newRoot;
            
            updateLowerNodes(root, levelDiff);
            return true;
        }
        else 
            return false;
    }
    
    public void updateLowerNodes(Node<File> startNode, int diff){
        
        numDirs = 0;
        numFiles = 0;
        
        LinkedStack<Node<File>> nodeStack = new LinkedStack<>();
        Node<File> currNode;
        nodeStack.push(startNode);
        
        while (!nodeStack.isEmpty() && !Thread.currentThread().isInterrupted()) {
        
            currNode = nodeStack.top();
            nodeStack.pop();
            if (currNode != null){
                currNode.setLevel(currNode.getLevel() + diff);
            }
            if (currNode != null && !currNode.getChildren().isEmpty()){
                numDirs++;
                Iterator childNodes  = currNode.getChildren().iterator();
                while(childNodes.hasNext()){
                    nodeStack.push((Node) childNodes.next());
                }
            }
            else 
                numFiles++;
            // otherwise the node is null, don't do anything with it
        }
        
    }
}
