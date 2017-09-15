package mike706574;

public class ConsumerException extends RuntimeException {
    public ConsumerException(String message) {
        super(message);
    }

    public ConsumerException(Throwable cause) {
        super(cause);
    }

    public ConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}