package mike706574;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String msg) {
        super(msg);
    }

    public FileNotFoundException(Throwable t) {
        super(t);
    }

    public FileNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}
