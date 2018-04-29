package au.edu.uofa.sei.assignment1.collector;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;
import au.edu.uofa.sei.assignment1.collector.type.Contributor;
import au.edu.uofa.sei.assignment1.collector.type.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used for extracting author lists from collected data base.
 *
 * The procedure is:
 * 1. get existing popular project lists
 * 2. get contributor lists (multiply pages)
 * 3. clone all repositories from all contributors
 * 4. do statistics on each contributor (indicators???)
 * 5. select valid contributors for final results
 */
public class AuthorNameExtractor extends CollectorCommon {
    public static void main(String[] args) throws Exception {
        // read the database content and extract the author names
        Conn c = new Conn(Constants.DB_NAME);
        QueryDb queryDb = new QueryDb(c);
        List<String> repos = LogWalker.getRepos(c);

        // find the last one existing in the database
        int idxRepo = 0;
        while (idxRepo < repos.size() && Contributor.checkProjectExistance(repos.get(idxRepo), queryDb)) idxRepo++;
        idxRepo --;

        // continue from where it was left
        Map<String, String> prevReq = null;
        for (int i = idxRepo; i < repos.size(); i ++) {
            prevReq = Contributor.collectingContributors(repos.get(i), prevReq, queryDb);
        }

        // load contributors from database, and loop all projects from each contributor:
        // from Contributor information
        // "type": "User"
        // "repos_url": "https://api.github.com/users/akarnokd/repos"



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
