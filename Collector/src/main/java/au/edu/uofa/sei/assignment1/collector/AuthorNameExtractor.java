package au.edu.uofa.sei.assignment1.collector;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;
import au.edu.uofa.sei.assignment1.collector.type.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for extracting author lists from collected data base.
 *
 * The procedure is:
 * 1. get existing popular project lists
 * 2. get contributor lists (multiply pages)
 * 3. do statistics on each contributor (indicators???)
 * 4. select valid contributors for final results
 */
public class AuthorNameExtractor extends CollectorCommon {
    public static void main(String[] args) throws Exception {
        // read the database content and extract the author names
        Conn c = new Conn(Constants.DB_NAME);
        List<String> authors = getAuthorList(c);
        // TODO:


    }

    static ArrayList<String> getAuthorList(Conn conn) throws SQLException {
        QueryDb queryDb = new QueryDb(conn);

        // TODO: for each project, get collaborator lists
        ArrayList<String> repoNames = new ArrayList<>();
        ResultSet rs = queryDb.select(Repository.TYPE);
        while (rs.next()) {
            String json = rs.getString("content");

            // use gson
            Gson gson = new GsonBuilder().create();
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            for (JsonElement ele : obj.getAsJsonArray("items")) {
                JsonObject repo = ele.getAsJsonObject();
                repoNames.add(repo.getAsJsonPrimitive("full_name").getAsString());
            }
        }
        return repoNames;
    }
}
