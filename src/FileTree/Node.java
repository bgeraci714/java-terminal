package FileTree;

import java.util.ArrayList;
import java.util.Objects;

public class Node<T> {
    private T data;
    private ArrayList<Node<T>> children;
    private int level;
    
    public Node(T info, int level){
        this.data = info;
        this.level = level;
        this.children = new ArrayList<>();
    }
    
    public void setLevel(int level){
        this.level = level;
    }
    
    public int getLevel(){
        return level;
    }
    
    public void addChild(Node<T> child){
        children.add(child);
    }
    
    public boolean removeChild(Node<T> child){  
        return children.remove(child);
    }
    
    public ArrayList< Node<T> > getChildren(){
        return children;
    }
    
    public void setData(T info){
        this.data = info;
    }
    
    public T getData(){
        return this.data;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        else if (obj == null || (this.getClass() != obj.getClass()))
            return false;
        else 
            return this.data.equals(((Node) obj).data);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.data);
        hash = 83 * hash + Objects.hashCode(this.children);
        return hash;
    }
    
    
}
