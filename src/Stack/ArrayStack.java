package Stack;

public class ArrayStack<T> implements StackInterface<T>{
    
    protected final int CAP = 100; // default capacity
    protected T[] stack; // holds stack elements
    protected int topIndex = -1; // initial topIndex when stack is empty
    
    public ArrayStack() {
        stack = (T[]) new Object[CAP];
    }
    public ArrayStack(int maxSize) {
        stack = (T[]) new Object[maxSize];
    }
    
    @Override
    public void push(T element) throws StackOverflowException {
        topIndex++;
        stack[topIndex] = element;
        
    }
    
    @Override
    public void pop() throws StackUnderflowException{
        if (isEmpty())
            throw new StackUnderflowException("Pop attempted on an empty stack.");
        else {
            stack[topIndex] = null;
            topIndex--;
        }
            
    }
    
    @Override
    public T top() throws StackUnderflowException {
        if (isEmpty())
            throw new StackUnderflowException("Top attempted on an empty stack");
        return stack[topIndex];
    }

    @Override
    public boolean isFull() {
        return topIndex == stack.length;
    }

    @Override
    public boolean isEmpty() {
        return topIndex == -1;
    }
}
