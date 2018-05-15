package au.edu.uofa.sei.assignment1.collector;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import au.edu.uofa.sei.assignment1.collector.db.PropertyDb;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;
import au.edu.uofa.sei.assignment1.collector.type.RepoCommitDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CommitDetailFetcher extends CollectorCommon {
    public static void main(String[] args) throws SQLException {
        Conn c = new Conn("commit.db");
        QueryDb queryDb = new QueryDb(c);
        PropertyDb propertyDb = new PropertyDb(c);

        // read system property
        int prevId = 0;
        String savedId = propertyDb.get(Constants.PROPERTY_COMMIT_DETAIL_FETCHING_ID);
        if (savedId != null) prevId = Integer.valueOf(savedId);


        // read from the `count` table
        final String SELECT = "SELECT * FROM count WHERE id > ? ORDER BY id;"; // make sure it's in order
        PreparedStatement select = c.getConn().prepareStatement(SELECT);
        select.setInt(1, prevId); // id is stored into properties when this id is finished collecting

        Map<String, String> prev = new HashMap<>();
        ResultSet resultSet = select.executeQuery();
        int counter = 0;
        while (resultSet.next()) {
            // get info
            counter ++;
            final int id = resultSet.getInt("id");
            final String username = resultSet.getString("username");
            final String project = resultSet.getString("project");
            final String hash = resultSet.getString("sha");
            System.err.println("Working on " + username + "/" + project + "," + hash);

            // turn off the auto commit
            c.getConn().setAutoCommit(false);

            // fetch this commit
            new RepoCommitDetail().makeRequest(0, username + "/" + project + "," + hash, prev, queryDb);

            // update the progress
            propertyDb.put(Constants.PROPERTY_COMMIT_DETAIL_FETCHING_ID, Integer.toString(id));

            // commit every 500 fetches
            if (counter % 500 == 0) {
                c.getConn().commit();
            }
        }

        // turn on back the auto commit
        c.getConn().commit();
        c.getConn().setAutoCommit(true);
    }
}
