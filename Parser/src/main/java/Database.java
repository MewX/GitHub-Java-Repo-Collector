import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private Connection connection = null;

    public Database(String filename) {
        connect(filename);
    }

    private void connect(String filename) {
        try {
            String url = "jdbc:sqlite:" + filename;

            connection = DriverManager.getConnection(url);

            // SQL statement for creating a new table
            String createTable = "CREATE TABLE IF NOT EXISTS dependencies (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  project TEXT NOT NULL,\n" +
                    "  commit_tag TEXT,\n" +
                    "  group_id TEXT NOT NULL,\n" +
                    "  artifact_id TEXT NOT NULL,\n" +
                    "  version TEXT NOT NULL,\n" +
                    "  UNIQUE(project, commit_tag, group_id, artifact_id) ON CONFLICT REPLACE \n" +
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkExistance(String project, String groupId, String artifactId, String version) {
        try {
//            String checkRecord = "SELECT count(*) FROM dependencies where project = ? and group_id = ? and artifact_id = ? and version = ?;";
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
}
