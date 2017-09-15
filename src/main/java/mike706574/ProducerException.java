package mike706574;

public class ProducerException extends RuntimeException {
    public ProducerException( String msg ) {
        super( msg );
    }

    public ProducerException( Throwable t ) {
        super( t );
    }

    public ProducerException( String msg, Throwable t ) {
        super( msg, t );
    }
}
