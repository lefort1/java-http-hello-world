package mike706574;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileRetriever {
    private static final Logger log = LoggerFactory.getLogger(FileRetriever.class);

    private final String host;
    private final Integer port;
    private final String username;
    private final String password;

    public FileRetriever(String host,
                         String username,
                         String password) {
        this(host, 21, username, password);
    }

    public FileRetriever(String host,
                         Integer port,
                         String username,
                         String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Optional<InputStream> stream(String path) {
        FTPClient client = new FTPClient();
        try {
            connect(client);
            InputStream is = client.retrieveFileStream(path);
            if (is == null) {
                if (client.getReplyCode() == 550) {
                    return Optional.empty();
                }
                throw new FileRetrieverException(client.getReplyString());
            }
            logout(client);
            return Optional.of(is);
        } catch (IOException ex) {
            throw new FileRetrieverException(ex);
        } finally {
            disconnect(client);
        }
    }

    public String slurp(String path) {
        return IO.slurp(stream(path).orElseThrow(() -> new FileNotFoundException(path)));
    }

    public List<FileInfo> listFiles(String path) {
        FTPClient client = new FTPClient();
        try {
            connect(client);
            List<FileInfo> files = Arrays.stream(client.listFiles(path))
                    .map(file -> {
                        Calendar timestamp = file.getTimestamp();
                        LocalDateTime time = LocalDateTime.ofInstant(timestamp.toInstant(),
                                ZoneId.systemDefault());
                        return new FileInfo(file.getName(),
                                file.getSize(),
                                time);
                    })
                    .collect(Collectors.toList());
            logout(client);
            return files;
        } catch (IOException ex) {
            throw new FileRetrieverException(ex);
        } finally {
            disconnect(client);
        }
    }

    public void download(String path, String destPath) {
        try (OutputStream os = new FileOutputStream("retrieved.dat")) {
            download(path, os);
        } catch (IOException ex) {
            throw new FileRetrieverException(ex);
        }
    }

    public void download(String path, OutputStream stream) {
        FTPClient client = new FTPClient();
        try {
            connect(client);
            boolean success = client.retrieveFile(path, stream);
            if (!success) {
                throw new FileRetrieverException("FTP download failed.");
            }
            logout(client);
        } catch (IOException ex) {
            throw new FileRetrieverException(ex);
        } finally {
            disconnect(client);
        }
    }

    public List<String> downloadAllLocally(String ftpDir,
                                           String pattern,
                                           String localDir) {
        FTPClient client = new FTPClient();
        try {
            connect(client);

            FTPFileFilter filter = new FTPFileFilter() {
                @Override
                public boolean accept(FTPFile ftpFile) {
                    return ftpFile.getName().contains(pattern);
                }
            };

            List<String> names = Arrays.stream(client.listFiles(ftpDir, filter))
                    .map(file -> file.getName())
                    .collect(Collectors.toList());

            for (String name : names) {
                String ftpPath = ftpDir + "/" + name;
                String localPath = localDir + "/" + name;
                try (OutputStream stream = new FileOutputStream(localPath)) {
                    boolean success = client.retrieveFile(ftpPath, stream);
                    if (!success) {
                        throw new FileRetrieverException("FTP download failed.");
                    }
                }
            }

            logout(client);
            return names;
        } catch (IOException ex) {
            throw new FileRetrieverException(ex);
        } finally {
            disconnect(client);
        }
    }

    private void logout(FTPClient client) {
        try {
            client.logout();
        } catch (IOException ex) {
            throw new FileRetrieverException(ex);
        }
    }

    private void disconnect(FTPClient client) {
        if (client.isConnected()) {
            try {
                client.disconnect();
            } catch (IOException ex) {
                throw new FileRetrieverException(ex);
            }
        }
    }

    private void connect(FTPClient client) {
        try {
            client.connect(host, port);
            client.login(username, password);
            System.out.println("Connected to " + host + ".");
            System.out.print(client.getReplyString());

            if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
                client.disconnect();
                throw new FileRetrieverException("FTP server refused connection.");
            }
        } catch (IOException ex) {
            throw new FileRetrieverException(ex);
        }
    }
}
