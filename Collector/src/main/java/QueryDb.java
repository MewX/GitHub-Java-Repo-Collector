import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QueryDb {
    private Connection conn = null;

    public QueryDb() { }

    public QueryDb(String fileName) throws SQLException {
        openConnection(fileName);
    }

    public void openConnection(String fileName) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + fileName);

        // TODO: init tables
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
