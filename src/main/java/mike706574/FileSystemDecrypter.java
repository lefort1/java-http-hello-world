package mike706574;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.callbacks.KeyringConfigCallbacks;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfig;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;
import org.bouncycastle.util.io.Streams;

import java.io.*;
import java.security.NoSuchProviderException;

public class FileSystemDecrypter {
    private final String publicKeyringPath;
    private final String secretKeyringPath;
    private final String passphrase;

    public FileSystemDecrypter(String publicKeyringPath,
                               String secretKeyringPath,
                               String passphrase) {
        this.publicKeyringPath = publicKeyringPath;
        this.secretKeyringPath = secretKeyringPath;
        this.passphrase = passphrase;
    }

    public void decrypt(String source, String dest) {
        final KeyringConfig keyringConfig = KeyringConfigs
                .withKeyRingsFromFiles(new File(publicKeyringPath),
                        new File(secretKeyringPath),
                        KeyringConfigCallbacks.withPassword(passphrase));

        try (final InputStream sourceStream = new FileInputStream(source);
             final OutputStream destOutput = new FileOutputStream(new File(dest));
             final BufferedOutputStream bufferedOutput = new BufferedOutputStream(destOutput);
             final InputStream decryptedStream = BouncyGPG.decryptAndVerifyStream()
                     .withConfig(keyringConfig)
                     .andIgnoreSignatures()
                     .fromEncryptedInputStream(sourceStream)) {
            Streams.pipeAll(decryptedStream, bufferedOutput);
        } catch (IOException | NoSuchProviderException ex) {
            throw new DecryptionException(ex);
        }
    }
}
