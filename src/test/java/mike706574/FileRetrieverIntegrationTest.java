package mike706574;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class FileRetrieverIntegrationTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private FileRetriever retriever;

    @Before
    public void setUp() {
        retriever = new FileRetriever("cadaman.nml.com",
                "EAS7510",
                password());
    }

    @Test
    public void stream() {
        OutputStream os = new ByteArrayOutputStream();
        assertEquals("foo\n",
                IO.slurp(retriever.stream("test/foo.txt").get()));
    }

    @Test
    public void streamFailure() {
        assertFalse(retriever.stream("elkawrjwa").isPresent());
    }

    private String password() {
        String encryptedPassword = IO.slurp("password.txt");
        return Obfuscator.decrypt(encryptedPassword, "secret");
    }
}
