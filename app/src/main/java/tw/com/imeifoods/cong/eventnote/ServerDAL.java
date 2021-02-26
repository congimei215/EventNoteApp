package tw.com.imeifoods.cong.eventnote;
import java.sql.Connection;

import static java.sql.DriverManager.getConnection;

/**
 * Created by user on 2017/7/10.
 */

public class ServerDAL {
    final String mDbIP = "172.17.0.132:1433";
    final String mDbName = "EVENTNOTE";
    final String mDbUser = "event";
    final String mDbPassword = "event123";

    protected Connection createConnection() {
        Connection vConnection = null;

        try {
            String  ConnectionURL = "jdbc:jtds:sqlserver://"
                    + mDbIP + ";"
                    + "databaseName=" + mDbName
                    + ";charset=utf8"
                    + ";user=" + mDbUser
                    + ";password=" + mDbPassword + ";";
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            vConnection = getConnection(ConnectionURL);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return vConnection;
    }
}


