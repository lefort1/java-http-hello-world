package mike706574;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClassPathLogger {
    private static final Logger log = LoggerFactory.getLogger(ClassPathLogger.class);

    public static void trace() {
        URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        String files = Arrays.stream(classLoader.getURLs())
                .map(url -> url.getFile())
                .collect(Collectors.joining("\n"));
        log.trace(String.format("Class Path:\n%s", files));
    }
}
