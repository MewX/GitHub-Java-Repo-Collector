package au.edu.uofa.sei.assignment1.db;

import java.sql.*;

public class QueryDb {
    private Connection conn = null;

    public QueryDb(String fileName) throws SQLException {
        openConnection(fileName);
    }

    public void openConnection(String fileName) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);

        final String TABLE_CREATING = "CREATE TABLE IF NOT EXISTS queries (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  type VARCHAR NOT NULL,\n" +
                "  time DATETIME DEFAULT current_timestamp,\n" +
                "  params TEXT NOT NULL,\n" +
                "  content TEXT\n" +
                ");";
        PreparedStatement create = conn.prepareStatement(TABLE_CREATING);
        create.execute();
    }

    public void insert(String type, String requestParams, String content) throws SQLException {
        final String INSERT = "INSERT INTO queries (type, params, content) VALUES (?, ?, ?);";
        PreparedStatement insert = conn.prepareStatement(INSERT);
        insert.setString(1, type);
        insert.setString(2, requestParams);
        insert.setString(3, content);
        insert.execute();
    }

    public ResultSet select(String type) throws SQLException {
        final String SELECT = "SELECT * FROM queries WHERE type = ?;";
        PreparedStatement select = conn.prepareStatement(SELECT);
        select.setString(1, type);
        return select.executeQuery();
    }

    public boolean checkExistance(String type, String requestParams) throws SQLException {
        final String CHECK = "select count(*) from queries where type = ? and params = ?;";
        PreparedStatement check = conn.prepareStatement(CHECK);
        check.setString(1, type);
        check.setString(2, requestParams);
        return check.executeQuery().getLong(1) != 0;
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
