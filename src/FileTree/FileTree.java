package FileTree;

import Stack.LinkedStack;
import java.io.File;
import java.util.Iterator;

public class FileTree extends Tree{
    @Override 
    public String toString(){
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
                // (all files with a parent of "src" were placed in the same subtree for example
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
}
