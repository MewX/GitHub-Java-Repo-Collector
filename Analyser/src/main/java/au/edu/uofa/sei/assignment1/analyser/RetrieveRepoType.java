package au.edu.uofa.sei.assignment1.analyser;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RetrieveRepoType {
    public static void main(String[] args) throws SQLException {
        final Conn repoTypeConnection = new Conn("repoType.db");
        final Conn repoConnection = new Conn("repo.db");

        // init db
        RepoTypeDB db = new RepoTypeDB(repoTypeConnection);
        // Query db
        QueryDb queryDb = new QueryDb(repoConnection);

        // get all list from database
        ArrayList<String> repoNames = new ArrayList<>();
        ArrayList<Integer> repoTypes = new ArrayList<>();
        ResultSet rs = queryDb.select("au.edu.uofa.sei.assignment1.type.Repository");
        while (rs.next()) {
            String json = rs.getString("content");

            // use gson
            Gson gson = new GsonBuilder().create();
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            for (JsonElement ele : obj.getAsJsonArray("items")) {
                JsonObject repo = ele.getAsJsonObject();
                repoNames.add(repo.getAsJsonPrimitive("full_name").getAsString());
                JsonObject owner = repo.getAsJsonObject("owner");
                String typeString = owner.getAsJsonPrimitive("type").getAsString();

                if (typeString.equals("Organization")) {
                    repoTypes.add(1);
                } else if (typeString.equals("User")) {
                    repoTypes.add(0);
                }
            }
        }

        for (int i = 0; i < repoNames.size(); i++) {
            db.insert(repoNames.get(i), repoTypes.get(i));
        }
    }
}
