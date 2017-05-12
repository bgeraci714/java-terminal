package Stack;

public interface StackInterface <T> {

    void push (T element) throws StackOverflowException;
    // throws a StackOverflowException if the stack is full
    // Otherwise, adds in a new element to the top of the stack. 
    
    void pop () throws StackUnderflowException;
    // throws a StackUnderflowException if the Stack is empty
    // Otherwise, removes the top element from the stack
    // commonly coupled with top for its classic definition
    
    T top () throws StackUnderflowException;
    // throws a StackUnderflowException if the Stack itself is empty
    // Otherwise, returns the top element of the Stack
    
    boolean isFull();
    // returns true if the stack is full, otherwise returns false
    
    boolean isEmpty();
    // returns true if the stack is empty, otherwise returns false
    
}


























