package au.edu.uofa.sei.assignment1.collector;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;
import au.edu.uofa.sei.assignment1.collector.type.Contributor;
import au.edu.uofa.sei.assignment1.collector.type.RepoCommit;
import au.edu.uofa.sei.assignment1.collector.type.UserRepo;
import com.google.gson.*;

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
 * 3. clone all repositories from each contributor
 * 4. get all commits via API from each user repo (record the user name and commit hash)
 * 5. do statistics on each commits made by this user, each commit made by this author should be extracted (indicators???)
 * 6. select valid contributors for final results
 */
public class AuthorNameExtractor extends CollectorCommon {
    static class UserInfo {
        public String userName, repoUrl;

        public UserInfo(String u, String r) {
            userName = u;
            repoUrl = r;
        }
    }

    public static void main(String[] args) throws Exception {
        // read the database content and extract the author names
        Conn c = new Conn(Constants.DB_NAME);
        QueryDb queryDb = new QueryDb(c);
        List<String> repos = LogWalker.getRepos(c); // 1. get project list

        // find the last one existing in the database
        int idxRepo = 0;
        final Contributor contributor = new Contributor();
        while (idxRepo < repos.size() && contributor.checkExistence(repos.get(idxRepo), queryDb)) idxRepo++;
        if (idxRepo > 0) idxRepo --;

        // continue from where it was left
        Map<String, String> prevReq = null;
        for (int i = idxRepo; i < repos.size(); i++) {
            prevReq = contributor.collect(repos.get(i), prevReq, queryDb); // 2. get contributor list
        }

        // load contributors from database, and loop all projects from each contributor:
        // from Contributor information
        // "type": "User"
        // "repos_url": "https://api.github.com/users/akarnokd/repos"
        List<UserInfo> contributorList = getContributorRepoLists(c);

        // get all repos of the users
        final UserRepo userRepo = new UserRepo();
        int idxUser = 0;
        while (idxUser < contributorList.size() && userRepo.checkExistence(contributorList.get(idxUser).userName, queryDb))
            idxUser++;
        if (idxUser > 0) idxUser--; // recover to the last one

        for (; idxUser < contributorList.size(); idxUser ++) {
            prevReq = userRepo.collect(contributorList.get(idxUser).userName, prevReq, queryDb);
        }

        // 4. get commits
        final RepoCommit repoCommit = new RepoCommit();
        int idxRepoCommit = 0;
        while (idxRepoCommit < repos.size() && repoCommit.checkExistence(repos.get(idxRepoCommit), queryDb))
            idxRepoCommit++;
        if (idxRepoCommit > 0) idxRepoCommit--;

        for (; idxRepoCommit < repos.size(); idxRepoCommit++) {
            prevReq = repoCommit.collect(repos.get(idxRepoCommit), prevReq, queryDb);
        }

        System.err.println("Done collecting");

        // get all repository list
        // execute Collector 2

        c.close();
    }

    static ArrayList<UserInfo> getContributorRepoLists(Conn conn) throws SQLException {
        QueryDb queryDb = new QueryDb(conn);

        // for each project, get collaborator lists
        ArrayList<UserInfo> results = new ArrayList<>();
        ResultSet rs = queryDb.select(new Contributor().TYPE);
        while (rs.next()) {
            String json = rs.getString("content"); // content column

            // use gson
            Gson gson = new GsonBuilder().create();
            for (JsonElement ele : gson.fromJson(json, JsonArray.class)) {
                JsonObject contributor = ele.getAsJsonObject();
                final String type = contributor.getAsJsonPrimitive("type").getAsString();
                final String userName = contributor.getAsJsonPrimitive("login").getAsString();
                final String reposUrl = contributor.getAsJsonPrimitive("repos_url").getAsString();
                if (type.equalsIgnoreCase("user")) {
                    results.add(new UserInfo(userName, reposUrl));
                }
            }
        }
        return results;
    }

}
