package au.edu.uofa.sei.assignment1;

import java.sql.*;

public class QueryDb {
    private Connection conn = null;

    public QueryDb(String fileName) throws SQLException {
        openConnection(fileName);
    }

    public void openConnection(String fileName) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);

        // TODO: init tables
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
    }

    public ResultSet select(String type) throws SQLException {
        final String SELECT = "SELECT * FROM queries WHERE type = ?;";
        PreparedStatement select = conn.prepareStatement(SELECT);
        select.setString(1, type);
        return select.executeQuery();
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
