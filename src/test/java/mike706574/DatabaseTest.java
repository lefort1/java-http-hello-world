package mike706574;

import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.jtds.jdbc.DefaultProperties;
import net.sourceforge.jtds.jdbc.Driver;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;
import org.junit.Ignore;
import org.junit.Test;

public class DatabaseTest {
    /*
      (def everything {:dbtype "jtds"
      :classname "net.sourceforge.jtds.jdbc.Driver";
      :host "kolar1.nml.com"
      :port 2328
      :dbname "invdms"
      :user "EAS7510"
      :password "SYBASE"})
    */

    @Test
    public void sybTest() {
        JtdsDataSource ds = new JtdsDataSource();
        ds.setServerName("kolar1.nml.com");
        ds.setPortNumber(2328);
        ds.setDatabaseName("invdms");
        ds.setUser("EAS7510");
        ds.setPassword("SYBASE");
        ds.setServerType(Driver.SYBASE);

        try(Connection conn = ds.getConnection()) {
            System.out.println(conn);
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void ssTest() {
        JtdsDataSource ds = new JtdsDataSource();
        ds.setServerName("NTDBTH4192M00.hotest.nmfco.com");
        ds.setPortNumber(1433);
        ds.setUser("EAS7510");
        ds.setPassword(password());
        ds.setDomain("nm");
        ds.setServerType(Driver.SQLSERVER);

        try(Connection conn = ds.getConnection()) {
            System.out.println(conn);
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void ssisTest() {
        JtdsDataSource ds = new JtdsDataSource();
        ds.setServerName("NTDBPH5868M00.nm.nmfco.com");
        ds.setPortNumber(1433);
        ds.setUser("EAS7510");
        ds.setPassword(password());
        ds.setDomain("nm");
        ds.setDatabaseName("master");
        ds.setServerType(Driver.SQLSERVER);

        try(Connection conn = ds.getConnection()) {
            System.out.println(conn);
        }
        catch(SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String password() {
        String encryptedPassword = IO.slurp("password.txt");
        return Obfuscator.decrypt(encryptedPassword, "secret");
    }


}
