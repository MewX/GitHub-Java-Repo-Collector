package au.edu.uofa.sei.assignment1.analyser.db;

import au.edu.uofa.sei.assignment1.collector.db.Conn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepoDB {
    private Conn conn;

    public RepoDB(Conn conn) throws SQLException {
        this.conn = conn;
    }

    public ResultSet selectUserProjectName() throws SQLException {
        final String SELECT = "select distinct dependencies.project from dependencies, repo_type where repo_type='User' and dependencies.project = repo_type.project;";
        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        return select.executeQuery();
    }

    public ResultSet selectOrganizationProjectName() throws SQLException {
        final String SELECT = "select distinct dependencies.project from dependencies, repo_type where repo_type='Organization' and dependencies.project = repo_type.project;";
        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        return select.executeQuery();
    }

    public ResultSet selectCommitsOfProject(String project) throws SQLException {
        final String SELECT = "select c.id, project, commit_tag, strftime('%Y-%m-%d', datetime(time / 1000, 'unixepoch')) as time " +
                "from (" +
                "select project, commit_tag from dependencies where project=? group by commit_tag) as d " +
                "inner join" +
                "  (select id, message, time from commits) as c " +
                "on d.commit_tag = c.message " +
                "order by c.id;";

        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        select.setString(1, project);
        return select.executeQuery();
    }

    public ResultSet selectDependency(String commit) throws SQLException {
        final String SELECT = "select * from dependencies where commit_tag = ?;";

        PreparedStatement select = conn.getConn().prepareStatement(SELECT);
        select.setString(1, commit);
        return select.executeQuery();
    }
}
