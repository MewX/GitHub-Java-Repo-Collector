package au.edu.uofa.sei.assignment2;

import au.edu.uofa.sei.assignment1.collector.db.Conn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommitDatabase {
    private Conn conn;

    public CommitDatabase(Conn conn) {
        this.conn = conn;
    }

    public ResultSet getCommitByUser(String username) throws SQLException {
        final String SELECT = "select username, count.project, count.sha, time, total, adding, deleting, fileschange " +
                "from commit_detail, count " +
                "where count.sha = commit_detail.sha and count.username = commit_detail.user " +
                "   and count.project = commit_detail.project and username = ?;";

        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        select.setString(1, username);
        return select.executeQuery();
    }
}
