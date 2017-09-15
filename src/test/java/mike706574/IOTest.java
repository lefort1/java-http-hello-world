package mike706574;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class IOTest {
    private static final Integer TEST_PORT = 9000;

    @Test
    public void fileSlurping() throws IOException {
        File tempFile = File.createTempFile( "test", ".tmp" );
        tempFile.deleteOnExit();

        try( Writer fileWriter = new FileWriter( tempFile ) ) {
            fileWriter.write( "test" );
        }

        String path = tempFile.getAbsolutePath();
        assertEquals( "test", IO.slurp( path ) );
    }

    @Test
    public void urlSlurping() {
        HttpServer server = run( TEST_PORT );

        try {
            String url = String.format( "http://localhost:%s/test", TEST_PORT );
            assertEquals( "test", IO.slurp( url ) );
        }
        finally {
            server.stop( 1 );
        }
    }

    private HttpServer run( Integer port ) {
        try {
            HttpServer server = HttpServer.create( new InetSocketAddress( port ), 0 );
            server.createContext( "/test", new TestHandler() );
            server.start();
            return server;
        }
        catch( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private static class TestHandler implements HttpHandler {
        @Override
        public void handle( HttpExchange exchange ) throws IOException {
            String response = "test";
            exchange.sendResponseHeaders( 200, response.length() );
            OutputStream os = exchange.getResponseBody();
            os.write( response.getBytes() );
            os.close();
        }
    }
}
