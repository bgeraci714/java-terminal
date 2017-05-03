package Queue;

public class ArrayBoundedQueue<T> implements QueueInterface<T>{

    protected final int CAP = 100; 
    protected T[] elements;
    protected int numElements = 0;
    protected int front = 0;
    protected int rear; // set to CAP/maxSize - 1 so that it will wrap 
                        // around as soon as a new element is added
    
    public ArrayBoundedQueue(){
        elements = (T[]) new Object[CAP];
        rear = CAP - 1; // need to figure out why this is (Found out, see above)
    }
    
    public ArrayBoundedQueue(int maxSize) {
        elements = (T[]) new Object[maxSize];
        rear = maxSize - 1; // need to figure out why this is
    }
    
    @Override
    public void enqueue(T element) throws QueueOverflowException {
        if (isFull())
            throw new QueueOverflowException("Enqueue attempted on a full queue");
        else {
            rear = (rear + 1) % elements.length; // % elements.length is the wrap around
            elements[rear] = element;
            numElements++;
        }
    }

    @Override
    public T dequeue() throws QueueUnderflowException {
       if (isEmpty())
           throw new QueueUnderflowException("Dequeue attempted on an empty queue");
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
        return numElements == elements.length;
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
