package mike706574;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.security.NoSuchProviderException;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.callbacks.KeyringConfigCallbacks;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfig;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;

import org.bouncycastle.util.io.Streams;

public class FileSystemDecrypter {
    private final String publicKeyringPath;
    private final String secretKeyringPath;
    private final String passphrase;

    public FileSystemDecrypter( String publicKeyringPath,
                                String secretKeyringPath,
                                String passphrase ) {
        this.publicKeyringPath = publicKeyringPath;
        this.secretKeyringPath = secretKeyringPath;
        this.passphrase = passphrase;
    }

    public void decrypt( String source, String dest ) {
        final KeyringConfig keyringConfig = KeyringConfigs
            .withKeyRingsFromFiles( new File( publicKeyringPath ),
                                    new File( secretKeyringPath ),
                                    KeyringConfigCallbacks.withPassword( passphrase ));

        try( final InputStream sourceStream = new FileInputStream( source );
             final OutputStream destOutput = new FileOutputStream( new File( dest ) );
             final BufferedOutputStream bufferedOutput = new BufferedOutputStream( destOutput );
             final InputStream decryptedStream = BouncyGPG.decryptAndVerifyStream()
                 .withConfig( keyringConfig )
                 .andIgnoreSignatures()
                 .fromEncryptedInputStream( sourceStream ) ) {
            Streams.pipeAll( decryptedStream, bufferedOutput );
        }
        catch( IOException | NoSuchProviderException ex ) {
            throw new DecryptionException( ex );
        }
    }
}
