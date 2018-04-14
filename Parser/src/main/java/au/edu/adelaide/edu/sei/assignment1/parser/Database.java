package au.edu.adelaide.edu.sei.assignment1.parser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private Connection connection = null;

    public static class Dependency {
        public String project, commitTag, groupId, artifactid, version;

        public Dependency(String project, String commitTag, String groupId, String artifactId, String version) {
            this.project = project;
            this.commitTag = commitTag;
            this.groupId = groupId;
            this.artifactid = artifactId;
            this.version = version;
        }
    }

    public Database(String filename) {
        this(filename, true);
    }

    public Database(String filename, boolean checkUnique) {
        connect(filename, checkUnique);
    }


    private void connect(String filename, boolean checkUnique) {
        try {
            String url = "jdbc:sqlite:" + filename;

            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(false);

            // SQL statement for creating a new table
            String createTable = "CREATE TABLE IF NOT EXISTS dependencies (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  project TEXT NOT NULL,\n" +
                    "  commit_tag TEXT,\n" +
                    "  group_id TEXT NOT NULL,\n" +
                    "  artifact_id TEXT NOT NULL,\n" +
                    "  version TEXT NOT NULL" +
                    (checkUnique ? ",\n  UNIQUE(project, commit_tag, group_id, artifact_id) ON CONFLICT REPLACE \n" : "") +
                    ");";

            PreparedStatement preparedStatement = connection.prepareStatement(createTable);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String project, String commitTag, String groupId, String artifactId, String version) {
        try {
            String insertRecord = "INSERT INTO dependencies (project, commit_tag, group_id, artifact_id, version) VALUES (?, ?, ?, ?, ?);";

            PreparedStatement preparedStatement = connection.prepareStatement(insertRecord);

            preparedStatement.setString(1, project);
            preparedStatement.setString(2, commitTag);
            preparedStatement.setString(3, groupId);
            preparedStatement.setString(4, artifactId);
            preparedStatement.setString(5, version);

            preparedStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> selectProjectNames() throws SQLException {
        final String SELECT = "SELECT DISTINCT project FROM dependencies ORDER BY id;";
        PreparedStatement select = connection.prepareStatement(SELECT);
        ResultSet resultSet = select.executeQuery();

        List<String> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(resultSet.getString("project"));
        }
        return list;
    }

    public List<Dependency> selectDependencies(String projectName) throws SQLException {
        final String SELECT = "SELECT * FROM dependencies WHERE project = ? ORDER BY id;";
        PreparedStatement select = connection.prepareStatement(SELECT);
        select.setString(1, projectName);
        ResultSet resultSet = select.executeQuery();

        List<Dependency> dependencies = new ArrayList<>();
        while (resultSet.next()) {
            dependencies.add(new Dependency(
                    resultSet.getString("project"),
                    resultSet.getString("commit_tag"),
                    resultSet.getString("group_id"),
                    resultSet.getString("artifact_id"),
                    resultSet.getString("version")
            ));
        }
        return dependencies;
    }

    public boolean checkExistance(String project, String groupId, String artifactId, String version) {
        try {
            String checkRecord = "SELECT count(*) FROM dependencies where project = ? and group_id = ? and artifact_id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(checkRecord);

            preparedStatement.setString(1, project);
            preparedStatement.setString(2, groupId);
            preparedStatement.setString(3, artifactId);
//            preparedStatement.setString(4, version);

            return preparedStatement.executeQuery().getLong(1) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean checkProjectExistance(String project) {
        try {
            String checkRecord = "SELECT count(*) FROM dependencies where project = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(checkRecord);

            preparedStatement.setString(1, project);

            return preparedStatement.executeQuery().getLong(1) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void close() throws SQLException {
        if (connection != null)
            connection.close();
    }
}
