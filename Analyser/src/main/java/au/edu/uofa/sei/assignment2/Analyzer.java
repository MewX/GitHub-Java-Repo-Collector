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
import java.util.*;

public class Analyzer {
    public static void main(String[] args) throws SQLException, IOException {
        final Conn connection = new Conn("/Users/nick/Documents/学习资料/2018.S1/SEI/Assignment/Research 2/data/commit.db");
        CommitDatabase db = new CommitDatabase(connection);

        List<String> userList = FileUtils.readLines(new File("user_list_valid.csv"));

//        String outputFilename = "results.csv";
        String outputFilename = "results_detail.csv";
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename, true));
//        writer.append("user, average active day/week, average project/week, average project/day, average commits/week, average adding/week, average deleting/week, average number of files/week, average focus/week\n");
        writer.append("user, week index, active day, projects, projects/day, focus, commits, adding, deleting, number of files\n");

        Map<Integer, Integer> dayCount = new HashMap<>();
        Map<Integer, Integer> hourCount = new HashMap<>();

        // loop all users
        for (String username : userList) {
            ResultSet resultSet = db.getCommitByUser(username);
//            ResultSet resultSet = db.getCommitByUser("fishercoder1534");

            boolean[] weekExist = new boolean[52];
            WeekCount[] weekCounts = new WeekCount[52];

            // loop all commits of this user
            while (resultSet.next()) {
                String project = resultSet.getString("project");
                String date = resultSet.getString("time");
                int total = resultSet.getInt("total");
                int adding = resultSet.getInt("adding");
                int deleting = resultSet.getInt("deleting");
                int numberOfFileChange = resultSet.getInt("fileschanged");

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

                // day didn't exist
                int dayIndex = dateTime.getDayOfWeek();
                if (weekCounts[weekIndex].activeDays.add(dayIndex)) {
                    Set<String> temp = new HashSet<>();
                    temp.add(project);

                    weekCounts[weekIndex].projectPerDay.put(dayIndex, temp);
                } else {
                    Set<String> temp = weekCounts[weekIndex].projectPerDay.get(dayIndex);
                    temp.add(project);

                    weekCounts[weekIndex].projectPerDay.put(dayIndex, temp);
                }

                // project didn't exist
                if (weekCounts[weekIndex].projects.add(project)) {
                    weekCounts[weekIndex].projectCommits.put(project, 1);
                } else {
                    int temp = weekCounts[weekIndex].projectCommits.get(project);
                    weekCounts[weekIndex].projectCommits.put(project, temp + 1);
                }

                weekCounts[weekIndex].numberOfFiles += numberOfFileChange;
                weekCounts[weekIndex].numberOfAdding += adding;
                weekCounts[weekIndex].numberOfDeleting += deleting;

                // total count
                if (dayCount.containsKey(dayIndex)) {
                    dayCount.put(dayIndex, dayCount.get(dayIndex) + 1);
                } else {
                    dayCount.put(dayIndex, 1);
                }

                int hourIndex = dateTime.getHourOfDay();
                if (hourCount.containsKey(hourIndex)) {
                    hourCount.put(hourIndex, hourCount.get(hourIndex) + 1);
                } else {
                    hourCount.put(hourIndex, 1);
                }
            }

            // summary data for the user, for average
//            int validWeek = 0;
//            int totalActiveDays = 0;
//            int totalProjectsWeek = 0;
//            float totalProjectsPerDay = 0;
//            int totalCommits = 0;
//            int totalAdding = 0;
//            int totalDeleting = 0;
//            int totalNumberOfFiles = 0;
//            float totalFocus = 0;
//
//            for (int j = 0; j < 52; j++) {
//                if (weekExist[j]) {
//                    WeekCount weekCount = weekCounts[j];
//
//                    totalActiveDays += weekCount.activeDays.size();
//                    totalProjectsWeek += weekCount.projects.size();
//                    totalProjectsPerDay += weekCount.getAverageProjectPerDay();
//                    totalCommits += weekCount.numberOfCommits;
//                    totalAdding += weekCount.numberOfAdding;
//                    totalDeleting += weekCount.numberOfDeleting;
//                    totalNumberOfFiles += weekCount.numberOfFiles;
//                    totalFocus += weekCount.getsFocus();
//
//                    validWeek++;
//                }
//            }
//
//            // output
//            System.out.println(username);
//            System.out.println("average active day/week: " + totalActiveDays * 1.0 / validWeek);
//            System.out.println("average project/week: " + totalProjectsWeek * 1.0 / validWeek);
//            System.out.println("average project/day: " + totalProjectsPerDay / validWeek);
//            System.out.println("average commits/week: " + totalCommits * 1.0 / validWeek);
//            System.out.println("average adding/week: " + totalAdding * 1.0 / validWeek);
//            System.out.println("average deleting/week: " + totalDeleting * 1.0 / validWeek);
//            System.out.println("average number of files/week: " + totalNumberOfFiles * 1.0 / validWeek);
//            System.out.println("average focus/week: " + totalFocus / validWeek);
//
//            String temp = username + "," + totalActiveDays * 1.0 / validWeek + "," +
//                    totalProjectsWeek * 1.0 / validWeek + "," + totalProjectsPerDay / validWeek + "," +
//                    totalCommits * 1.0 / validWeek + "," + totalAdding * 1.0 / validWeek + "," +
//                    totalDeleting * 1.0 / validWeek + "," + totalNumberOfFiles * 1.0 / validWeek + ", " +
//                    totalFocus / validWeek;

//            writer.append(temp);
//            writer.append("\n");

            // detailed weekly data
            for (int j = 0; j < 52; j++) {
                if (weekExist[j]) {
                    WeekCount weekCount = weekCounts[j];

                    String temp = username + "," + (weekCount.weekIndex+1) + "," + weekCount.activeDays.size() + "," +
                            weekCount.projects.size() + "," + weekCount.getAverageProjectPerDay() + "," +
                            weekCount.getsFocus() + "," + weekCount.numberOfCommits + "," +
                            weekCount.numberOfAdding + "," + weekCount.numberOfDeleting + "," +
                            weekCount.numberOfFiles;

                    writer.append(temp);
                    writer.append("\n");
                }
            }
        }

        // total data
//        writer.append("\n\n");
//        writer.append("total: \n");
//
//        for (Map.Entry<Integer, Integer> entry : dayCount.entrySet()) {
//            writer.append(entry.getKey() + ", " + entry.getValue() + "\n");
//        }
//
//        writer.append("\n\n");
//
//        for (Map.Entry<Integer, Integer> entry : hourCount.entrySet()) {
//            writer.append(entry.getKey() + ", " + entry.getValue() + "\n");
//        }

        writer.close();
    }
}
