package Queue;

public class QueueUnderflowException extends RuntimeException{
    public QueueUnderflowException() {
        super();
    }
    
    public QueueUnderflowException(String message) {
        super(message);
    }
}
