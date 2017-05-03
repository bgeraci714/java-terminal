package Queue;

public class ArrayUnboundedQueue<T> implements QueueInterface<T>{
    protected final int DEFCAP = 100;
    protected int origCap;
    protected T[] elements;
    protected int numElements = 0;
    protected int front = 0;
    protected int rear;
    
    public ArrayUnboundedQueue() {
        elements = (T[]) new Object[DEFCAP];
        rear = DEFCAP - 1;
        origCap = DEFCAP;
    }
    
    public ArrayUnboundedQueue(int origCap) {
        elements = (T[]) new Object[origCap];
        rear = origCap - 1;
        this.origCap = origCap;
    }
    
    private void enlarge(){
        System.out.println("Enlarge was called!");
        // enlarges the elements array by the original cap amount
        T[] larger = (T[]) new Object[elements.length + origCap];
        
        int curSmaller = front;
        for (int curLarger = 0; curLarger < numElements; curLarger++) {
            larger[curLarger] = elements[curSmaller]; // follows the original queue from its start point
                                                      // until the end filling up the larger array
                                                      // starting at 0 for its index 
                                                      // in a sense, it resets the larger array
            curSmaller = (curSmaller + 1) % elements.length; // wrap around
        }
        
        // updates the instance variables
        elements = larger;
        front = 0;
        rear = numElements - 1; // points to the previous end of the queue
    }
    
    @Override
    public void enqueue(T element) {
        if (numElements == elements.length) // basically the isFull() check
            enlarge();
        rear = (rear + 1) % elements.length;
        elements[rear] = element;
        numElements++;
    }

    @Override
    public T dequeue() throws QueueUnderflowException {
        if (isEmpty())
            throw new QueueUnderflowException("Dequeue called on an empty queue");
        else {
            T toReturn = elements[front];
            elements[front] = null;
            front = (front + 1) % elements.length;
            numElements--;
            return toReturn;
        }
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return numElements == 0;
    }

    @Override
    public int size() {
        return numElements;
    }
    

}
