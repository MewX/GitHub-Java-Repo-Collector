package au.edu.uofa.sei.assignment2;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * get all users appears in the repoCommit meeting certain conditions
 */
public class UserFilter {
    public static void main(String[] args) throws SQLException, IOException {
        final Conn connection = new Conn("/Users/nick/Documents/学习资料/2018.S1/SEI/Assignment/Research 2/data/repo.db");
        Database db = new Database(connection);


        String outputFilename = "count.csv";

//        outputUserList(db);

        List<String> userList = FileUtils.readLines(new File("output.txt"));

        // loop all users
        for (int j = 0; j < userList.size(); j++) {
            if ((j + 1) % 500 == 0) {
                System.err.println("Progress: " + (j+1) + "/" + userList.size());
            }

            String temp = userList.get(j);
            ResultSet resultSet = db.getCommitsByUser(temp);

            int userCommitCount = 0;
            Map<Integer, Integer> monthCount = new HashMap<>();
            List<String> commitList = new ArrayList<>();

            // loop all commits of a user
            while (resultSet.next()) {
                String json = resultSet.getString("content");
                String params = resultSet.getString("params");

                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(json);

                if (jsonElement.isJsonArray()) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();

                    // loop all commits
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonElement element = jsonArray.get(i);
                        JsonObject commitObject = element.getAsJsonObject();

                        String sha = commitObject.get("sha").getAsString();
                        String date = commitObject.get("commit").getAsJsonObject().get("author").getAsJsonObject().get("date").getAsString();

                        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                        LocalDateTime dateTime = formatter.parseLocalDateTime(date);

                        if (dateTime.getYear() == 2017) {
                            userCommitCount ++;

                            int month = dateTime.getMonthOfYear();
                            if (monthCount.containsKey(month)) {
                                monthCount.put(month, monthCount.get(month) + 1);
                            } else {
                                monthCount.put(month, 1);
                            }

                            // get project
                            String project = params.split("/")[1];

                            String stringBuilder = temp + "," +
                                    project + "," +
                                    date + "," +
                                    sha;
                            commitList.add(stringBuilder);
                        }
                    }
                }
            }

            if (userCommitCount > 180 && greaterThanThreshold(monthCount)) {
                System.out.println(temp + " " + userCommitCount);

                // output to file
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename, true));

                for (String tempCommit : commitList) {
                    writer.append(tempCommit);
                    writer.append("\n");
                }

                writer.close();
            }
        }
    }

    private static boolean greaterThanThreshold(Map<Integer, Integer> count) {
        if (count.size() < 12) {
            return false;
        }

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() < 15) {
                return false;
            }
        }
        return true;
    }

    private static void outputUserList(Database db) throws SQLException, IOException {
        Set<String> userSet = new HashSet<>();

        ResultSet resultSet = db.getUserList();

        while (resultSet.next()) {
            String[] split = resultSet.getString("params").split("/");
            userSet.add(split[0]);
        }

        List<String> userList = new ArrayList<>(userSet);

        System.out.println(userList.size());

        Path out = Paths.get("output.txt");
        Files.write(out, userList);
    }
}
