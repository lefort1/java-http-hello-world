package mike706574;

import java.io.IOException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;

public class ShellDecrypter {
    public final static class ExecutionOutput {
        private Integer exit;
        private String out;
        public ExecutionOutput( Integer exit, String out ) {
            this.exit = exit;
            this.out = out;
        }
        public Integer getExit() { return exit; }
        public String getOut() { return out; }
    }

    private final String passphrase;

    public ShellDecrypter( String passphrase ) {
        this.passphrase = passphrase;
    }

    public void decrypt( String source, String dest ) {

        String command = String.format( "gpg --batch -v -v --yes --always-trust --no-tty --skip-verify --no-mdc-warning -o %s --passphrase \"%s\" -d %s",
                                        dest,
                                        passphrase,
                                        source );
        executeAndPrint( "pwd" );
        executeAndPrint( command );
        executeAndPrint( "pwd" );
    }

    public static void executeAndPrint( String command ) {
        System.out.println( "Executing: " + command );
        ExecutionOutput output = execute( command );
        System.out.println( "Exit:" );
        System.out.println( output.getExit() );
        System.out.println( "Out:" );
        System.out.println( output.getOut() );
        System.out.flush();
    }

    public static ExecutionOutput execute( String command ) {
        try {
            StringBuffer output = new StringBuffer();

            Process process = Runtime.getRuntime().exec( command );
            process.waitFor();
            try( InputStreamReader isReader = new InputStreamReader( process.getInputStream() );
                 BufferedReader reader = new BufferedReader( isReader ) ) {
                String line = "";
                while ( ( line = reader.readLine() ) != null ) {
                    output.append(line + "\n");
                }

                return new ExecutionOutput( process.exitValue(),
                                            output.toString() );
            }
        }
        catch( IOException | InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
