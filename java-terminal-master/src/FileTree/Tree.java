package FileTree;

import Queue.LinkedQueue;
import Stack.LinkedStack;
import java.util.ArrayList;
import java.util.Iterator;

public class Tree<T> {
    protected Node<T> root;
    protected int findLevel;
    
    
    public Tree(){
        root = null;
    }
    
    // can have trouble if two parents have the same name. Unique parents are needed
    // or some way to move down to the correct parent (this is handled in FileTree)
    public boolean add(T parent, T child){
        // find the parent using breadth first search
        
        // need to handle just in case the root is empty
        if (root == null){
            root = new Node<>(child, 0);
            return true;
        }
        Node<T> foundParent = find(parent);
        if (foundParent == null){
            return false;
        }
        else {
            foundParent.addChild(new Node<>(child, findLevel));
            return true;
        }
    }
    
    public T getRootData(){
        return root.getData();
    }
    
    public boolean appendTree(Node<T> node){
        T rootData = (T) node.getData();
        Node<T> parentNode = findParentOf(rootData);
        if (parentNode != null){
            
            int insertLevel = parentNode.getLevel() + 1;
            
            ArrayList<Node<T>> children = parentNode.getChildren();
            
            for (int i = 0; i < children.size(); i++){
                
                if (children.get(i).getData().equals(rootData)){
                    
                    children.set(i, node);
                    updateLowerLevels(parentNode.getChildren().get(i), insertLevel);
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public boolean reRootDown(T data){
        Node<T> newRoot = find(data);
        if (newRoot != null){
            int levelDiff = root.getLevel() - newRoot.getLevel();
            root = newRoot;
            
            updateLowerLevels(root, levelDiff);
            return true;
        }
        else 
            return false;
    }
    
    public void updateLowerLevels(Node<T> startNode, int diff){
        
        LinkedStack<Node<T>> nodeStack = new LinkedStack<>();
        Node<T> currNode;
        nodeStack.push(startNode);
        
        while (!nodeStack.isEmpty()) {
        
            currNode = nodeStack.top();
            nodeStack.pop();
            if (currNode != null){
                currNode.setLevel(currNode.getLevel() + diff);
            }
            if (currNode != null && !currNode.getChildren().isEmpty()){
                Iterator childNodes  = currNode.getChildren().iterator();
                while(childNodes.hasNext()){
                    nodeStack.push((Node) childNodes.next());
                }
            }
            // otherwise the node is null, don't do anything with it
        }
        
    }
    
    public Node<T> find(T data){
        
        findLevel = 1;
        LinkedQueue<Node<T>> nodeQueue = new LinkedQueue<>();
        LinkedQueue<Node<T>> nextQueue = new LinkedQueue<>();
        
        Node<T> currNode;
        nodeQueue.enqueue(root);
        while (!nodeQueue.isEmpty() || !nextQueue.isEmpty()) {
            
            if (nodeQueue.isEmpty()){
                nodeQueue = nextQueue;
                nextQueue = new LinkedQueue<>();
                findLevel++;
            }
            
            currNode = nodeQueue.dequeue();
            if (currNode != null && currNode.getData().equals(data)){
                return currNode;
            }
            else if (currNode != null){
                Iterator childNodes  = currNode.getChildren().iterator();
                while(childNodes.hasNext()){
                    nextQueue.enqueue((Node) childNodes.next());
                }
                
            }
            // otherwise the node is null, don't do anything with it
        }
        
        return null;
    }
    
    public Node<T> findParentOf(T data){
        
        findLevel = 1;
        LinkedQueue<Node<T>> nodeQueue = new LinkedQueue<>();
        LinkedQueue<Node<T>> nextQueue = new LinkedQueue<>();
        
        Node<T> parentNode;
        nodeQueue.enqueue(root);
        while (!nodeQueue.isEmpty() || !nextQueue.isEmpty()) {
            
            if (nodeQueue.isEmpty()){
                nodeQueue = nextQueue;
                nextQueue = new LinkedQueue<>();
                findLevel++;
            }
            
            parentNode = nodeQueue.dequeue();
            
            if (parentNode != null){
                System.out.println(parentNode.getChildren());
                Iterator childNodes  = parentNode.getChildren().iterator();
                while(childNodes.hasNext()){
                    System.out.println("Has next?");
                    Node<T> nextNode = (Node) childNodes.next();
                    System.out.println("Next Node data: " + nextNode.getData());
                    if (nextNode.getData().equals(data))
                        return parentNode;
                    nextQueue.enqueue(nextNode);
                }
                
            }
            // otherwise the node is null, don't do anything with it
        }
        
        return null;
    }
    
    public boolean isEmpty()
    {
        return (root == null);
    }
    
    // depth first search style handling of nodes
    public String toString(){
        String result = "";
        
        LinkedStack<Node<T>> nodeStack = new LinkedStack<>();
        Node<T> currNode;
        nodeStack.push(root);
        
        while (!nodeStack.isEmpty()) {
        
            currNode = nodeStack.top();
            System.out.print(currNode.getData() + " ");
            nodeStack.pop();
            if (currNode != null){
                result += "\n";
                for(int i = 0; i < currNode.getLevel(); i++ )
                    result += "---";
                result += currNode.getData().toString();
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
