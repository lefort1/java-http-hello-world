package mike706574;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FileRetrieverTest {
    private static final String USER = "bob";
    private static final String PASSWORD = "password";
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private FakeFtpServer fakeFtpServer;
    private FileRetriever retriever;

    @Before
    public void setUp() {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0);
        fakeFtpServer.addUserAccount(new UserAccount(USER, PASSWORD, "c:\\home"));

        FileSystem fileSystem = new WindowsFakeFileSystem();
        fileSystem.add(new DirectoryEntry("c:\\home"));
        fileSystem.add(new FileEntry("c:\\home\\test\\foo.txt", "foo."));
        fileSystem.add(new FileEntry("c:\\home\\test\\bar.txt", "bar!!"));
        fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.start();

        retriever = new FileRetriever("localhost",
                fakeFtpServer.getServerControlPort(),
                USER,
                PASSWORD);
    }

    @After
    public void tearDown() {
        fakeFtpServer.stop();
    }

    @Test
    public void stream() {
        OutputStream os = new ByteArrayOutputStream();
        assertEquals("foo.",
                IO.slurp(retriever.stream("test\\foo.txt")));
    }

    @Test
    public void failedDownload() {
        thrown.expect(FileRetrieverException.class);

        OutputStream os = new ByteArrayOutputStream();
        new FileRetriever("foo.bar.baz",
                "foo",
                "bar")
                .download("foo", os);
    }

    @Test
    public void listFiles() throws Exception {
        List<FileInfo> files = retriever.listFiles("test");

        assertEquals(2, files.size());

        FileInfo foo = files.get(0);
        assertEquals("foo.txt", foo.getName());
        assertEquals(new Long(4), foo.getSize());

        FileInfo bar = files.get(1);
        assertEquals("bar.txt", bar.getName());
        assertEquals(new Long(5), bar.getSize());
    }

    @Test
    public void download() {
        OutputStream out = new ByteArrayOutputStream();
        retriever.download("test\\foo.txt",
                out);

        assertEquals("foo.", out.toString());
    }

    private String password() {
        String encryptedPassword = IO.slurp("password.txt");
        return Obfuscator.decrypt(encryptedPassword, "secret");
    }
}
