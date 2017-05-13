package Queue;

public interface QueueInterface<T> {
    
    
    void enqueue(T element) throws QueueOverflowException;
    // throws queue overflow exception if full otherwise adds the element
    
    
    T dequeue() throws QueueUnderflowException;
    // throws queue underflow exception if empty otherwise 
    // removes the front of the queue and returns it
    
    boolean isFull();
    
    boolean isEmpty();
    
    
    int size();
    // returns the number of elements in the queue
    
}
