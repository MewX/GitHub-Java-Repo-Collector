package au.edu.uofa.sei.assignment1;

import au.edu.uofa.sei.assignment1.db.CommitDb;
import au.edu.uofa.sei.assignment1.db.Conn;
import au.edu.uofa.sei.assignment1.db.QueryDb;
import au.edu.uofa.sei.assignment1.type.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class loop through all commits and store in database
 *
 * The following data should be record:
 * 0. index
 * 1. projectname (key): owner/name
 * 2. commit date
 * 3. commit message
 * 4. commit index
 * 5. commit author name
 * 6. commit author email
 */
public class LogWalker {
    public static void main(String[] args) throws SQLException, IOException, GitAPIException {
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "INFO");
        PropertyConfigurator.configure(prop);

        Conn c = new Conn(Constants.DB_NAME);
        CommitDb commitDb = new CommitDb(c);

        // get all list from database
        ArrayList<String> repoNames = getRepos(c); // the order does not change
//        List<String> repoNames = Collections.singletonList("DDDM-Assignments"); // the order does not change

        // find the last repo that was fetched and continue
        int i = 0;
        for (; i < repoNames.size(); i ++) {
            if (!commitDb.checkExistance(repoNames.get(i))) break;
            System.err.println("Found record: " + repoNames.get(i));
        }

        // now
        i = i > 0 ? i - 1 : 0;
        System.err.println("Cleaning record from project: " + repoNames.get(i));
        commitDb.cleanProject(repoNames.get(i));
        for (; i < repoNames.size(); i ++) {
//            System.err.println("Processing " + repoNames.get(i) + " ...");
            loopThroughRepo(repoNames.get(i), commitDb);
        }

        c.close();
    }

    private static ArrayList<String> getRepos(Conn conn) throws SQLException {
        QueryDb queryDb = new QueryDb(conn);

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

    private static void loopThroughRepo(String repoName, CommitDb commitDb) throws IOException, GitAPIException, SQLException {
        final String treeNamePrefix = "refs/heads/";
        final String gitPath = Constants.BASE_PATH + repoName + "/.git";

        System.err.println("Working on " + gitPath);
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        org.eclipse.jgit.lib.Repository repository = builder
                .setGitDir(new File(gitPath))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
        Git git = new Git(repository);

        ArrayList<CommitDb.Commit> commits = new ArrayList<>();
        for (RevCommit commit : git.log().all().call()) {
            System.out.println("    Found commit: " + commit.getName());
            final PersonIdent authorId = commit.getAuthorIdent();
            commits.add(new CommitDb.Commit(repoName, new Timestamp(authorId.getWhen().getTime()), commit.getName(), 0, authorId.getEmailAddress()));
        }

        // sort ascending
        commits.sort(Comparator.comparing(a -> a.time));

        // number all of them
        for (int i = 0; i < commits.size(); i ++) {
            commits.get(i).commitId = i + 1;
            commitDb.insert(commits.get(i));
        }
    }
}
