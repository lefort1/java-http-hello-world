package mike706574;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fun.mike.frontier.alpha.FileTransferClient;
import fun.mike.frontier.alpha.FileTransferException;

public class PositionsMain {
    private static final Logger log = LoggerFactory.getLogger(PositionsMain.class);

    public static void main(String[] args) {
        ClassPathLogger.trace();
        CryptoConfigurator.configure();

        Map<String, String> config = new HashMap<String, String>();
        config.put("url", "testftp.bbh.com");
        config.put("ftpUsername", "infnmltest");
        config.put("ftpPassword", "inf1nmlt2003");
        config.put("ftpPath", "frominfo/INF_SUB_NML_TRADES_T.20170830064509.pgp");
        config.put("publicKeyringPath", "/home/invstmtt/.gnupg/pubring.gpg");
        config.put("passphrase", "NM_BBHi_07-28-2015");
        if (args.length == 2) {
            config.put("date", args[1]);
        }

        run(config);
    }

    public static void run(Map<String, String> config) {
        log.info("Starting execution.");

        // Configuration
        String url = Prop.required(config, "url");
        String username = Prop.required(config, "ftpUsername");
        String password = Prop.required(config, "ftpPassword");
        String publicKeyringPath = Prop.required(config, "publicKeyringPath");
        String secretKeyringPath = Prop.required(config, "secretKeyringPath");
        String passphrase = Prop.required(config, "passphrase");

        String date = Prop.optional(config, "date").orElse(today());

        // Build dependencies
        FileTransferClient client = new FileTransferClient(url, username, password);
        FileSystemDecrypter decrypter = new FileSystemDecrypter(publicKeyringPath,
                secretKeyringPath,
                passphrase);

        // Run process
        String path = "INF_SUB_NML_TRADES_T." + date + ""; // TODO
        try {
            client.optionalDownload(path, "encrypted.gpg");
        }
        catch(FileTransferException ex) {
            throw new RuntimeException(ex);
        }
        decrypter.decrypt("encrypted.gpg", "decrypted.dat");

        List<List<String>> trades = IO.slurpHeadlessDelimited("decrypted.dat",
                "|");

        BigDecimal fee = calculateFee(trades);
        String feeText = String.format("%.2f", fee);
        List<List<String>> tradesWithFee = trades.stream()
                .map(trade -> {
                    trade.set(26, feeText);
                    return trade;
                })
                .collect(Collectors.toList());

        IO.spitHeadlessDelimited("trades_with_fee.dat", "|", tradesWithFee);

        // weird sas process
        // go on message queue

        log.info("Finished execution.");
    }

    private static String today() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static BigDecimal calculateFee(List<List<String>> trades) {
        return trades.stream()
                .map(trade -> new BigDecimal(trade.get(26)))
                .reduce(new BigDecimal(0.0),
                        (fee, amt) -> fee.add(amt));
    }
}
