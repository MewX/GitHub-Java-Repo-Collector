package au.edu.uofa.sei.assignment1.collector.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * database connection - singleton
 */
public class Conn {
    private Connection conn;

    public Conn(final String fileName) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);
    }

    public Connection getConn() {
        return conn;
    }

    public void close() throws SQLException {
        if (conn != null) conn.close();
    }
}
