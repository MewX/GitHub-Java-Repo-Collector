package au.edu.uofa.sei.assignment1.collector.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PropertyDb {
    private Conn conn;

    public PropertyDb(Conn conn) throws SQLException {
        this.conn = conn;
        openConnection();
    }

    public void openConnection() throws SQLException {
        final String TABLE_CREATING = "CREATE TABLE IF NOT EXISTS properties (\n" +
                "  name TEXT PRIMARY KEY,\n" +
                "  value TEXT,\n" +
                "  time DATETIME DEFAULT current_timestamp\n" +
                ");";
        PreparedStatement create = conn.getConn().prepareStatement(TABLE_CREATING);
        create.execute();
    }

    public void put(String key, String value) throws SQLException {
        final String INSERT_OR_UPDATE = "INSERT OR REPLACE INTO properties(name, value, time) VALUES (?, ?, current_timestamp);";
        PreparedStatement insert = conn.getConn().prepareStatement(INSERT_OR_UPDATE);
        insert.setString(1, key);
        insert.setString(2, value);
        insert.execute();
    }

    /**
     * @param key the key
     * @return null if not exist, otherwise the value
     */
    public String get(String key) throws SQLException {
        final String SELECT = "select * from properties where name = ?;";
        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        select.setString(1, key);

        ResultSet resultSet = select.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("value");
        } else {
            return null;
        }
    }

}
