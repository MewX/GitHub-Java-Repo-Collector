package au.edu.uofa.sei.assignment1.analyser;

import au.edu.uofa.sei.assignment1.collector.db.Conn;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RepoTypeDB {
    private Conn conn;

    public RepoTypeDB(Conn conn) throws SQLException {
        this.conn = conn;
        openConnection();
    }

    public void openConnection() throws SQLException {
        final String TABLE_CREATING = "CREATE TABLE IF NOT EXISTS repo_type (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  project TEXT NOT NULL,\n" +
                "  repo_type TEXT \n" +
                ");";
        PreparedStatement create = conn.getConn().prepareStatement(TABLE_CREATING);
        create.execute();
    }

    public void insert(String project, String type) throws SQLException {
        final String INSERT = "INSERT INTO repo_type (project, repo_type) VALUES (?, ?);";
        PreparedStatement insert = conn.getConn().prepareStatement(INSERT);
        insert.setString(1, project);
        insert.setString(2, type);
        insert.execute();
    }
}
