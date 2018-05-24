package au.edu.uofa.sei.assignment2;

import au.edu.uofa.sei.assignment1.collector.db.Conn;
import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings("Duplicates")
public class HeatMap {
    public static void main(String[] args) throws SQLException, IOException {
        final Conn connection = new Conn("/Users/nick/Documents/学习资料/2018.S1/SEI/Assignment/Research 2/data/commit.db");
        CommitDatabase db = new CommitDatabase(connection);

        List<String> userList = FileUtils.readLines(new File("user_list_valid.csv"));

        String outputFilename = "heat_map.csv";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename, true));

        writer.append("user,");

        for (int i = 1; i < 53; i++) {
            writer.append(String.valueOf(i));
            writer.append(",");
        }
        writer.append("\n");

        // loop all users
        int count = 1;
        for (String username : userList) {
            System.out.println(username + "(" + count + "/90" + ")");
            ResultSet resultSet = db.getCommitByUser(username);

            boolean[] weekExist = new boolean[52];
            WeekCount[] weekCounts = new WeekCount[52];

            // loop all commits of this user
            while (resultSet.next()) {
                String date = resultSet.getString("time");

                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
                LocalDateTime dateTime = formatter.parseLocalDateTime(date);

                int weekIndex = dateTime.getWeekOfWeekyear() - 1;
                // see if week index already exist
                if (!weekExist[weekIndex]) {
                    WeekCount weekCount = new WeekCount(weekIndex);
                    weekCounts[weekIndex] = weekCount;

                    weekExist[weekIndex] = true;
                }

                weekCounts[weekIndex].numberOfCommits++;
            }

            // detailed weekly data
            String temp = username + ",";
            for (int j = 0; j < 52; j++) {
                if (weekExist[j]) {
                    WeekCount weekCount = weekCounts[j];

                    temp += weekCount.numberOfCommits + ",";
                } else {
                    temp += "0, ";
                }
            }

            writer.append(temp);
            writer.append("\n");

            count++;
        }

        writer.close();
    }
}
