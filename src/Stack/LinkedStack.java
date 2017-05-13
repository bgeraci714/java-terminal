package Stack;

import Support.LLNode;

public class LinkedStack<T> implements StackInterface<T>{
    protected LLNode<T> top;
    
    public LinkedStack () {
        top = null;
    }
    
    @Override
    public void push (T element) throws StackOverflowException{
        if (isFull()) {
            throw new StackOverflowException("Somehow blew the stack with push!");
        }
        else {
            LLNode<T> newNode = new LLNode<T>(element);
            newNode.setLink(top);
            top = newNode;
        }
    }
    
    @Override
    public void pop () throws StackUnderflowException {
        if (isEmpty())
            throw new StackUnderflowException("Tried to pop the stack with nothing in it.");
        else {
            top = top.getLink();
        }
    } 
    
    @Override
    public T top () throws StackUnderflowException {
        if (isEmpty())
            throw new StackUnderflowException("Tried to call top on an empty stack.");
        else 
            return top.getInfo();
    }
    
    @Override
    public boolean isEmpty() {
        return top == null;
    }
    
    @Override
    public boolean isFull() {
        return false;
    }
    
}
