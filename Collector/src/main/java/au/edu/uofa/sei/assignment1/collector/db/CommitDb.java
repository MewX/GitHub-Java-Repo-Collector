package au.edu.uofa.sei.assignment1.collector.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommitDb {
    public static class Commit {
        public String project, msg, author;
        public Timestamp time;
        public int commitId;

        public Commit(String project, Timestamp time, String msg, int commitId, String author) {
            this.project = project;
            this.time = time;
            this.msg = msg;
            this.commitId = commitId;
            this.author = author;
        }
    }

    private Conn conn;

    public CommitDb(Conn conn) throws SQLException {
        this.conn = conn;
        openConnection();
    }

    public void openConnection() throws SQLException {
        final String TABLE_CREATING = "CREATE TABLE IF NOT EXISTS commits (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  project TEXT NOT NULL,\n" +
                "  time DATETIME,\n" +
                "  message TEXT,\n" +
                "  commit_id INTEGER NOT NULL,\n" +
                "  author TEXT -- author email used for identifying the author\n" +
                ");";
        PreparedStatement create = conn.getConn().prepareStatement(TABLE_CREATING);
        create.execute();
    }

    public void insert(Commit commit) throws SQLException {
        insert(commit.project, commit.time, commit.msg, commit.commitId, commit.author);
    }

    public void insert(String project, Timestamp time, String msg, int commitId, String author) throws SQLException {
        final String INSERT = "INSERT INTO commits (project, time, message, commit_id, author) VALUES (?, ?, ?, ?, ?);";
        PreparedStatement insert = conn.getConn().prepareStatement(INSERT);
        insert.setString(1, project);
        insert.setTimestamp(2, time);
        insert.setString(3, msg);
        insert.setInt(4, commitId);
        insert.setString(5, author);
        insert.execute();
    }

    public ResultSet select(String project) throws SQLException {
        final String SELECT = "SELECT * FROM commits WHERE project = ? ORDER BY commit_id ASC;";
        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        select.setString(1, project);
        return select.executeQuery();
    }

    public List<Commit> resultToCommit(ResultSet resultSet) throws SQLException {
        List<Commit> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(new Commit(
                    resultSet.getString("project"),
                    resultSet.getTimestamp("time"),
                    resultSet.getString("message"),
                    (int) resultSet.getLong("commit_id"),
                    resultSet.getString("author")
            ));
        }
        return list;
    }

    public boolean checkExistance(String project) throws SQLException {
        final String CHECK = "select count(*) from commits where project = ?;";
        PreparedStatement check = conn.getConn().prepareStatement(CHECK);
        check.setString(1, project);
        return check.executeQuery().getLong(1) != 0;
    }

    public void cleanProject(String project) throws SQLException {
        final String CLEAN = "DELETE FROM commits WHERE project = ?;";
        PreparedStatement clean = conn.getConn().prepareStatement(CLEAN);
        clean.setString(1, project);
        clean.execute();
    }
}
