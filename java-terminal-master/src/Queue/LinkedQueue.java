package Queue;

import Support.LLNode;

public class LinkedQueue<T> implements QueueInterface<T> {
    protected LLNode<T> front;
    protected LLNode<T> rear;
    protected int numElements = 0;
    
    public LinkedQueue(){
        front = null;
        rear = null;
    }
    
    @Override
    public void enqueue(T element){
        LLNode<T> newNode = new LLNode<T>(element);
        if (rear == null)
            front = newNode;
        else    
            rear.setLink(newNode);
        
        rear = newNode;
        numElements++;
    }

    @Override
    public T dequeue() throws QueueUnderflowException {
        if (isEmpty())
            throw new QueueUnderflowException("Dequeue attempted on empty queue.");
        else {
            T toReturn = front.getInfo();
            front = front.getLink();
            if (front == null) 
                rear = null;
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
        return front == null;
    }

    @Override
    public int size() {
        return numElements;
    }

}
