package eu.mizerak.alemiz.imgbin.provider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnectionInfo {

    private final String host;
    private final String database;
    private final String user;
    private final String password;

    public MySQLConnectionInfo(String host, String database, String user, String password){
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public Connection getMySqlInstance() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://"+this.host+"/" + this.database+"?allowPublicKeyRetrieval=true&useSSL=false",
                this.user,
                this.password);
    }

    public String getHost() {
        return this.host;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }
}
