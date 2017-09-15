package mike706574;

import java.io.IOException;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradesMain {
    private static final Logger log = LoggerFactory.getLogger( TradesMain.class );

    public static void main( String[] args ) {
        ClassPathLogger.trace();
        CryptoConfigurator.configure();

        Map<String, String> config = new HashMap<String, String>();
        config.put( "url", "testftp.bbh.com" );
        config.put( "ftpUsername", "infnmltest" );
        config.put( "ftpPassword", "inf1nmlt2003" );
        config.put( "ftpPath", "frominfo/INF_SUB_NML_TRADES_T.20170830064509.pgp" );
        config.put( "publicKeyringPath", "/home/invstmtt/.gnupg/pubring.gpg" );
        config.put( "passphrase", "NM_BBHi_07-28-2015" );
        if( args.length == 2 ) {
            config.put( "date", args[1] );
        }

        run( config );
    }

    private static String requiredProperty( Map<String, String> config, String key ) {
        if( !config.containsKey( key ) ) {
            String message = String.format( "Missing required configuration property: %s",
                                            key );
            throw new NoSuchElementException( message );
        }

        String value = config.get( key );

        if( value.trim().equals( "" ) ) {
            String message = String.format( "Value for configuration property %s was blank.",
                                            key );
            throw new IllegalArgumentException( message );
        }

        return value;
    }

    private static Optional<String> optionalProperty( Map<String, String> config, String key ) {
        if( !config.containsKey( key ) ) {
            return Optional.empty();
        }

        String value = config.get( key );

        if( value.trim().equals( "" ) ) {
            String message = String.format( "Value for configuration property %s was blank.",
                                            key );
            throw new IllegalArgumentException( message );
        }

        return Optional.of( value );
    }

    public static void run( Map<String, String> config ) {
        log.info( "Starting execution." );

        // Configuration
        String url = requiredProperty( config, "url" );
        String username = requiredProperty( config, "ftpUsername" );
        String password = requiredProperty( config, "ftpPassword" );
        String publicKeyringPath = requiredProperty( config, "publicKeyringPath" );
        String secretKeyringPath = requiredProperty( config, "secretKeyringPath" );
        String passphrase = requiredProperty( config, "passphrase" );

        String date = optionalProperty( config, "date" ).orElse( today() );

        // Build dependencies
        FileRetriever fileRetriever = new FileRetriever( url, username, password );
        FileSystemDecrypter decrypter = new FileSystemDecrypter( publicKeyringPath,
                                                                 secretKeyringPath,
                                                                 passphrase );

        // Run process
        String path = "INF_SUB_NML_TRADES_T." + date + ""; // TODO
        fileRetriever.download( path, "encrypted.gpg" );
        decrypter.decrypt( "encrypted.gpg", "decrypted.dat" );

        List<List<String>> trades = IO.slurpHeadlessDelimited( "decrypted.dat",
                                                               "|" );

        BigDecimal fee = calculateFee( trades );
        String feeText = String.format( "%.2f", fee );
        List<List<String>> tradesWithFee = trades.stream()
            .map( trade -> {
                    trade.set( 26, feeText );
                    return trade;
                } )
            .collect( Collectors.toList() );

        IO.spitHeadlessDelimited( "trades_with_fee.dat", "|", tradesWithFee );

        // weird sas process
        // go on message queue

        log.info( "Finished execution." );
    }

    private static String today() {
        return LocalDate.now().format( DateTimeFormatter.BASIC_ISO_DATE );
    }

    public static BigDecimal calculateFee( List<List<String>> trades ) {
        return trades.stream()
            .map( trade -> new BigDecimal( trade.get( 26 ) ) )
            .reduce( new BigDecimal( 0.0 ),
                     (fee, amt) -> fee.add( amt ) );
    }
}
