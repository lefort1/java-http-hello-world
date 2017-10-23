package mike706574;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.security.Security;

import static org.junit.Assert.assertEquals;

public class FileSystemDecrypterTest {
    @Before
    public void setUp() {
        IO.mkdir("work/");
    }

    @After
    public void tearDown() {
        IO.nuke("work/");
    }

    @Test
    public void decrypting() throws Exception {
        IO.copy("FileSystemDecrypterTest/encrypted.gpg",
                "work/encrypted.gpg");

        final String publicKeyringPath = "secret/pubring.gpg";
        final String secretKeyringPath = "secret/secring.gpg";
        final String passphrase = "NM_BBHi_07-28-2015";

        FileSystemDecrypter decrypter = new FileSystemDecrypter(publicKeyringPath,
                secretKeyringPath,
                passphrase);

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        decrypter.decrypt("work/encrypted.gpg",
                          "work/decrypted.dat");

        assertEquals(IO.slurp("expected/decrypted.dat"),
                     IO.slurp("work/decrypted.dat"));
    }
}
