package au.edu.uofa.sei.assignment1;

import au.edu.uofa.sei.assignment1.type.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class Collector {
    static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
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
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "INFO");
        PropertyConfigurator.configure(prop);

        // init db
        QueryDb db = new QueryDb("repo.db");

        // collecting list (disabled when not required)
//        Repository.collectingRepos(db);

        // get all list from database
        ArrayList<String> repoNames = new ArrayList<>(), repoUrls = new ArrayList<>();
        ResultSet rs = db.select(Repository.TYPE);
        while (rs.next()) {
            String json = rs.getString("content");

            // use gson
            Gson gson = new GsonBuilder().create();
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            for (JsonElement ele : obj.getAsJsonArray("items")) {
                JsonObject repo = ele.getAsJsonObject();
                repoNames.add(repo.getAsJsonPrimitive("full_name").getAsString());
                repoUrls.add(repo.getAsJsonPrimitive("git_url").getAsString());
            }
        }

        // clear temp folder, clone to temp folder first, them move to the real folder
        final String TEMP_PATH = "temp/";
        deleteFolder(new File(TEMP_PATH));
        new File(TEMP_PATH).mkdirs();

        // clone all
        final String BASE_PATH = "java-repos/";
        new File(BASE_PATH).mkdirs();
        for (int i = 0; i < repoNames.size(); i ++) {
            String finalFolderName = BASE_PATH + repoNames.get(i);
            String tempFolderName = TEMP_PATH + repoNames.get(i);
            if (!new File(finalFolderName).exists()) {
                // clone
                System.err.format("Cloning %s into %s...\n", repoNames.get(i), tempFolderName);
                Git result = Git.cloneRepository()
                        .setURI(repoUrls.get(i))
                        .setDirectory(new File(tempFolderName))
                        .call();
                result.close();

                // move back
                System.err.format("Moving %s back to %s...\n", tempFolderName, finalFolderName);
                Files.move(new File(tempFolderName).toPath(), new File(finalFolderName).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }

    }
}
