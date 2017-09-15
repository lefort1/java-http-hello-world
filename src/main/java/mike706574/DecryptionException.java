package mike706574;

public class DecryptionException extends RuntimeException {
    public DecryptionException( String msg ) {
        super( msg );
    }

    public DecryptionException( Throwable t ) {
        super( t );
    }

    public DecryptionException( String msg, Throwable t ) {
        super( msg, t );
    }
}
