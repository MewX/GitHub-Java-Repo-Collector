package au.edu.uofa.sei.assignment1.collector;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;
import au.edu.uofa.sei.assignment1.collector.type.Contributor;
import au.edu.uofa.sei.assignment1.collector.type.RepoCommit;
import au.edu.uofa.sei.assignment1.collector.type.Repository;
import au.edu.uofa.sei.assignment1.collector.type.UserRepo;
import com.google.gson.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used for extracting author lists from collected data base.
 * <p>
 * The procedure is:
 * 1. get existing popular project lists
 * 2. get contributor lists (multiply pages)
 * 3. clone all repositories from each contributor
 * 4. get all commits via API from each user repo (record the user name and commit hash)
 * 5. do statistics on each commits made by this user, each commit made by this author should be extracted:
 * (this time, we only collect from API request only, without collecting the huge file system-based git repositories)
 * - project name
 * - user login (need to match committer/login)
 * - commit/sha, the hash of that commit
 * - commit/committer/date
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
        Map<String, String> prevReq = collectContributorList(repos, queryDb, null);
        List<UserInfo> contributorList = getContributorRepoLists(c);

        // get all repos of the users
        prevReq = collectUserRepoList(contributorList, queryDb, prevReq);
        // update repos with user name
        repos = getRepos(c);
        System.err.println("INFO - Found " + repos.size() + " user projects.");

        // 4. get commits
        // here collect only the user's commits
        collectRepoCommitList(repos, queryDb, prevReq);
        System.err.println("Done collecting");

        // get all repository list
        // execute Collector 2

        c.close();
    }

    /**
     * load contributors from database, and loop all projects from each contributor:
     * from Contributor information
     * "type": "User"
     * "repos_url": "https://api.github.com/users/akarnokd/repos"
     */
    private static ArrayList<UserInfo> getContributorRepoLists(Conn conn) throws SQLException {
        QueryDb queryDb = new QueryDb(conn);

        // for each project, get collaborator lists
        ArrayList<UserInfo> results = new ArrayList<>();
        ResultSet rs = queryDb.select(new Contributor().TYPE);
        while (rs.next()) {
            String json = rs.getString("content"); // content column

            // use gson
            try {
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
            } catch (Exception e) {
                // by experiments: Gson does not throw JsonSyntaxException
                e.printStackTrace();
                System.err.println("Error in content: " + json);
            }
        }
        return results;
    }

    private static Map<String, String> collectContributorList(List<String> repos, QueryDb queryDb, Map<String, String> prevReq) throws SQLException {
        int idxRepo = 0;
        final Contributor contributor = new Contributor();
        while (idxRepo < repos.size() && contributor.checkExistence(repos.get(idxRepo), queryDb)) idxRepo++;
        if (idxRepo > 0) idxRepo--;

        // continue from where it was left
        for (int i = idxRepo; i < repos.size(); i++) {
            prevReq = contributor.collect(repos.get(i), prevReq, queryDb); // 2. get contributor list
        }
        return prevReq;
    }

    private static Map<String, String> collectUserRepoList(List<UserInfo> contributorList, QueryDb queryDb, Map<String, String> prevReq) throws SQLException {
        final UserRepo userRepo = new UserRepo();
        int idxUser = 0;
        while (idxUser < contributorList.size() && userRepo.checkExistence(contributorList.get(idxUser).userName, queryDb))
            idxUser++;
        if (idxUser > 0) idxUser--; // recover to the last one

        for (; idxUser < contributorList.size(); idxUser++) {
            prevReq = userRepo.collect(contributorList.get(idxUser).userName, prevReq, queryDb);
        }
        return prevReq;
    }

    private static Map<String, String> collectRepoCommitList(List<String> repos, QueryDb queryDb, Map<String, String> prevReq) throws SQLException {
        final RepoCommit repoCommit = new RepoCommit();
        int idxRepoCommit = 0;
        while (idxRepoCommit < repos.size() && repoCommit.checkExistence(buildRepoCommitKey(repos.get(idxRepoCommit)), queryDb))
            idxRepoCommit++;
        if (idxRepoCommit > 0) idxRepoCommit--;

        for (; idxRepoCommit < repos.size(); idxRepoCommit++) {
            prevReq = repoCommit.collect(buildRepoCommitKey(repos.get(idxRepoCommit)), prevReq, queryDb);
        }
        return prevReq;
    }

    private static String buildRepoCommitKey(String projectName) {
        String userName = projectName.substring(0, projectName.indexOf("/"));
        return projectName + "," + userName;
    }

    static ArrayList<String> getRepos(Conn conn) throws SQLException {
        QueryDb queryDb = new QueryDb(conn);

        ArrayList<String> repoNames = new ArrayList<>();
        ResultSet rs = queryDb.select(new UserRepo().TYPE);
        while (rs.next()) {
            String json = rs.getString("content");

            // use gson
            Gson gson = new GsonBuilder().create();
            for (JsonElement ele : gson.fromJson(json, JsonArray.class)) {
                JsonObject repo = ele.getAsJsonObject();
                repoNames.add(repo.getAsJsonPrimitive("full_name").getAsString());
            }
        }
        return repoNames;
    }

}
