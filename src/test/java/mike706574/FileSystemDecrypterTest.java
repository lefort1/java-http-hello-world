package mike706574;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class FileSystemDecrypterTest {
    @Before
    public void setUp() {
        new File( "out/" ).mkdir();
    }

    @After
    public void tearDown() {
        IO.nukeDirectory( "out/" );
    }

    @Test
    public void decrypting() throws Exception {
        final String publicKeyringPath = "secret/pubring.gpg";
        final String secretKeyringPath = "secret/secring.gpg";
        final String passphrase = "NM_BBHi_07-28-2015";

        FileSystemDecrypter decrypter = new FileSystemDecrypter( publicKeyringPath,
                                                                 secretKeyringPath,
                                                                 passphrase );

        if( Security.getProvider( BouncyCastleProvider.PROVIDER_NAME ) == null ) {
            Security.addProvider( new BouncyCastleProvider() );
        }

        decrypter.decrypt( "in/encrypted.gpg",
                           "out/decrypted.dat" );

        assertEquals( IO.slurp( "expected/decrypted.dat" ),
                      IO.slurp( "out/decrypted.dat" ) );

    }
}
