package au.edu.uofa.sei.assignment1.collector;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;
import au.edu.uofa.sei.assignment1.collector.type.Repository;
import au.edu.uofa.sei.assignment1.collector.type.UserRepo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Collector extends CollectorCommon {

    static class ProjectInfo {
        public String name, gitUrl;

        public ProjectInfo(String name, String gitUrl) {
            this.name = name;
            this.gitUrl = gitUrl;
        }
    }


    static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static void main(String[] args) throws SQLException, GitAPIException, IOException {
        // curl -iH 'User-Agent: UofA SEI 2018 Project' https://api.github.com -u mseopt:mseoptpassword2018
        // curl -i 'https://api.github.com/users/whatever?client_id=Iv1.ad6b73b7b61c26f3&client_secret=9ac009128eb89e8c2b0f9d39fddd0378d3dfbdc0'
        // curl 'https://api.github.com/user/repos?page=2&per_page=100'
        // if exceeded: 403 Forbidden -> exit directly

        // because github allows searching for 1000 results only,
        // therefore, using star size limit can expand the searching results:
        // https://api.github.com/search/repositories?q=language:java+stars:%3C1534&sort=stars&order=desc&per_page=100&page=10
        final Conn c = new Conn(Constants.DB_NAME);

        // init db
        QueryDb db = new QueryDb(c);

        // collecting list (disabled when not required)
//        Repository.collectingRepos(db);

        // get all list from database
        String type = Repository.TYPE;
        if (args.length > 0 && Integer.valueOf(args[0]) == 2) type = new UserRepo().TYPE;
        ArrayList<ProjectInfo> repos = getProjectInfoList(type, db);
        cloneAll(repos);

        c.close();
    }

    public static ArrayList<ProjectInfo> getProjectInfoList(final String type, QueryDb db) throws SQLException {
        ArrayList<ProjectInfo> repos = new ArrayList<>();
        ResultSet rs = db.select(type);
        while (rs.next()) {
            String json = rs.getString("content");

            // use gson
            Gson gson = new GsonBuilder().create();
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            for (JsonElement ele : obj.getAsJsonArray("items")) {
                JsonObject repo = ele.getAsJsonObject();
                repos.add(new ProjectInfo(repo.getAsJsonPrimitive("full_name").getAsString(),
                        repo.getAsJsonPrimitive("git_url").getAsString()));
            }
        }
        return repos;
    }

    public static void cloneAll(List<ProjectInfo> repos) throws IOException, GitAPIException {
        // clear temp folder, clone to temp folder first, them move to the real folder
        deleteFolder(new File(Constants.TEMP_PATH));
        new File(Constants.TEMP_PATH).mkdirs();

        // clone all
        new File(Constants.BASE_PATH).mkdirs();
        for (int i = 0; i < repos.size(); i++) {
            String finalFolderName = Constants.BASE_PATH + repos.get(i).name;
            String tempFolderName = Constants.TEMP_PATH + repos.get(i).name;
            if (!new File(finalFolderName).exists()) {
                // clone
                System.err.format("(%d/%d) Cloning %s into %s...\n", i, repos.size(), repos.get(i).name, tempFolderName);
                Git result = Git.cloneRepository()
                        .setURI(repos.get(i).gitUrl)
                        .setDirectory(new File(tempFolderName))
                        .call();
                result.close();

                // move back
                System.err.format("Moving %s back to %s...\n", tempFolderName, finalFolderName);
                new File(finalFolderName).mkdirs();
                Files.move(new File(tempFolderName).toPath(), new File(finalFolderName).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
