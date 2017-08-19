package mike706574;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import java.net.URL;

public class AppTest {
    @Test
    public void sayingHello() {
        App.main( new String[] {} );
        assertEquals( "The server should say hello.",
                      "Hello, world!",
                      slurp( "http://localhost:8000/hello" ) );
    }

    private String slurp( String path ) {
        try( InputStream is = new URL( path ).openConnection().getInputStream() ) {
            return slurp( is );
        }
        catch( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private String slurp( InputStream is ) {
        try( Reader isReader = new InputStreamReader( is, "UTF-8" );
             Reader reader = new BufferedReader( isReader ) ) {
            StringBuilder stringBuilder = new StringBuilder();
            int c = 0;
            while( ( c = reader.read() ) != -1 ) {
                stringBuilder.append( (char)c );
            }

            return stringBuilder.toString();
        }
        catch( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
