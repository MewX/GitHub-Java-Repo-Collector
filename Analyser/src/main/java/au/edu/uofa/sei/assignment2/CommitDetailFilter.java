package au.edu.uofa.sei.assignment2;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Filter useful information from commit detail json message
 */
public class CommitDetailFilter {
    public static void main(String[] args) throws SQLException, IOException {
        final Conn connection = new Conn("/Users/nick/Documents/学习资料/2018.S1/SEI/Assignment/Research 2/data/commit.db");
        Database db = new Database(connection);

        String outputFilename = "commit detail.csv";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename, true));

        ResultSet resultSet = db.getCommitDetail();

        int count = 0;
        final int totalCount = 157676;
        // loop through all results
        while (resultSet.next()) {
            if (count % 5000 == 0) {
                System.err.println("Progress: " + count + "/" + totalCount);
            }

            String json = resultSet.getString("content");
            String params = resultSet.getString("params");

            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(json);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.get("stats") == null) {
                System.out.println(params);
                continue;
            }

            JsonObject statsObject = jsonObject.get("stats").getAsJsonObject();
            JsonArray filesArray = jsonObject.get("files").getAsJsonArray();

            String sha, user, project;
            int total, adding, deleting, numOfChangeFiles;

            sha = jsonObject.get("sha").getAsString();
            user = params.split("/")[0];
            project = params.split("/")[1];
            total = statsObject.get("total").getAsInt();
            adding = statsObject.get("additions").getAsInt();
            deleting = statsObject.get("deletions").getAsInt();
            numOfChangeFiles = filesArray.size();

            String temp = user + "," + project + "," + sha + "," + total + "," + adding + "," + deleting + "," + numOfChangeFiles;

            writer.append(temp);
            writer.append("\n");

            count++;
        }

        writer.close();
    }
}
