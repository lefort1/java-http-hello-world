package mike706574;

import java.io.UnsupportedEncodingException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Obfuscator {
    public static String encrypt( String text, String key ) {
        try {
            byte[] bytes = getBytes( text );
            Cipher cipher = getCipher( Cipher.ENCRYPT_MODE, key );
            return Base64.encodeBase64String( cipher.doFinal( bytes ) );
        }
        catch( BadPaddingException | IllegalBlockSizeException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public static String decrypt( String text, String key ) {
        try {
            Cipher cipher = getCipher( Cipher.DECRYPT_MODE, key );
            return new String( cipher.doFinal( Base64.decodeBase64( text ) ), "UTF-8" );
        }
        catch( BadPaddingException |
               IllegalBlockSizeException |
               UnsupportedEncodingException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private static byte[] getBytes( String string ) {
        try {
            return string.getBytes( "UTF-8" );
        }
        catch( UnsupportedEncodingException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private static byte[] getRawKey( String seed ) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance( "AES" );
            SecureRandom secureRandom = SecureRandom.getInstance( "SHA1PRNG" );
            secureRandom.setSeed( getBytes( seed ) );
            keyGenerator.init( 128, secureRandom );
            return keyGenerator.generateKey().getEncoded();
        }
        catch( NoSuchAlgorithmException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private static Cipher getCipher( int mode, String seed ) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec( getRawKey( seed ),
                                                             "AES" );
            Cipher cipher = Cipher.getInstance( "AES" );
            cipher.init( mode, secretKeySpec );
            return cipher;
        }
        catch( InvalidKeyException |
               NoSuchAlgorithmException |
               NoSuchPaddingException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
