package mike706574;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Arrays;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassPathLogger {
    private static final Logger log = LoggerFactory.getLogger( ClassPathLogger.class );

    public static void trace() {
        URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        String files = Arrays.stream( classLoader.getURLs() )
            .map( url -> url.getFile() )
            .collect( Collectors.joining( "\n" ) );
        log.trace( String.format( "Class Path:\n%s", files ) );
    }
}
