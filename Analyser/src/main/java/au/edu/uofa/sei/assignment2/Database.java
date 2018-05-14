package au.edu.uofa.sei.assignment2;

import au.edu.uofa.sei.assignment1.collector.db.Conn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private Conn conn;

    public Database(Conn conn) {
        this.conn = conn;
    }

    public ResultSet getUserList() throws SQLException {
//        final String SELECT = "select params from queries where type = 'UserRepo';";
        final String SELECT = "select params from queries where type = 'RepoCommit' and content != '[]';";

        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        return select.executeQuery();
    }

    public ResultSet getCommitsByUser(String username) throws SQLException {
        final String SELECT = "select * from queries where type = 'RepoCommit' and params like ? and content !='[]';";

        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        select.setString(1, "%/commits?author=" + username + "&%");
        return select.executeQuery();
    }
}
