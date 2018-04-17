package au.edu.uofa.sei.assignment1.analyser;

import au.edu.uofa.sei.assignment1.analyser.db.RepoDB;
import au.edu.uofa.sei.assignment1.collector.db.Conn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@SuppressWarnings("Duplicates")
public class CalculateVersionTrend {
    public static void main(String[] args) throws SQLException, IOException, ParseException, InterruptedException {
        final Conn connection = new Conn("repo.db");
        RepoDB repoDB = new RepoDB(connection);

        String[] userArtifact = {"gradle", "appcompat-v7", "junit", "design", "support-v4", "recyclerview-v7", "android-maven-gradle-plugin", "gradle-bintray-plugin", "library", "cardview-v7"};
        String[] organizationArtifact = {"junit", "gradle", "appcompat-v7", "mockito-core", "slf4j-api", "guava", "gson", "support-v4", "commons-io", "oss-parent"};

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        // loop Top 10 frequently used artifact by user
        for (int i = 9; i < 10; i++) {
//            String artifactName = userArtifact[i];
            String artifactName = organizationArtifact[i];

//            System.err.println("Processing User: " + artifactName + " (" + (i + 1) + "/10)");
            System.err.println("Processing Organization: " + artifactName + " (" + (i + 1) + "/10)");

            // initialize result set
            List<List<Map<String, Integer>>> result = new ArrayList<>();

            // initialize result set
            for (int k = 2008; k < 2018; k++) {
                List<Map<String, Integer>> yearList = new ArrayList<>();

                for (int j = 0; j < 4; j++) {
                    Map<String, Integer> quarterMap = new HashMap<>();
                    yearList.add(quarterMap);
                }

                result.add(yearList);
            }

            // get all commits with the artifact
//            ResultSet resultSet = repoDB.selectProjectVersion("User", artifactName);
            ResultSet resultSet = repoDB.selectProjectVersion("Organization", artifactName);

            // loop through all commits
            while (resultSet.next()) {
                // convert date
                String commitDate = resultSet.getString("commit_time");
                Date date = simpleDateFormat.parse(commitDate);
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                int year = localDate.getYear();
                int month = localDate.getMonthValue();
                String version = resultSet.getString("version");

//              version = normalizeVersion(version);

                // before 2008, after 2017, skip
                if (year < 2008 || year > 2017 || version.equals("skip")) {
                    continue;
                }

                List<Map<String, Integer>> yearList = result.get(year - 2008);
                Map<String, Integer> quarterMap = yearList.get(getQuarterByMonth(month)-1);

                // check if version exist
                if (quarterMap.containsKey(version)) {
                    int count = quarterMap.get(version);
                    count++;

                    quarterMap.put(version, count);
                } else {
                    quarterMap.put(version, 1);
                }
            }

            // output results
//            BufferedWriter writer = new BufferedWriter(new FileWriter("User-" + artifactName + ".csv", true));
            BufferedWriter writer = new BufferedWriter(new FileWriter("Organization-" + artifactName + ".csv", true));

            // year
            int count = 1;
            for (int j = 0; j < result.size(); j++) {
                List<Map<String, Integer>> yearList = result.get(j);

                // quarter
                for (int k = 0; k < 4; k++) {
                    Map<String, Integer> quarter = yearList.get(0);
                    for (Map.Entry<String, Integer> entry : quarter.entrySet()){
                        System.out.println((j+2008) + "Q" + (k+1) + ": " + entry.getKey() + " " + entry.getValue());

                        writer.append(String.valueOf(j + 2008));
                        writer.append("Q");
                        writer.append(String.valueOf(k + 1));
                        writer.append(",");
                        writer.append(String.valueOf(count));
                        writer.append(",");
                        writer.append(entry.getKey());
                        writer.append(",");
                        writer.append(String.valueOf(entry.getValue()));
                        writer.append("\n");
                    }

                    count++;
                }

            }

            writer.close();

            Thread.sleep(5000);
        }
    }

    private static String normalizeVersion(String version) {
        String[] split = version.split("\\.");

        if (version.startsWith("$") || version.equals("default") || version.equals("selectordialog") ||version.equals("latest.release") || version.equals("actor-jvm") || version.equals("drag-sort-listview") || split.length < 2) {
            return "skip";
        }

        version = version.replace("[", "").replace(")", "").replace(",", "");

        if (split[split.length - 1].startsWith("+")) {
            version = version.replace("+", "0");
        }


        if (version.contains("+")) {
            version = version.replace("+", "");
        }

        if (version.contains("-")) {
            version = version.substring(0, version.lastIndexOf("-"));
        }

        if (version.contains("@")) {
            version = version.substring(0, version.lastIndexOf("@"));
        }

        if (split.length == 3) {
            version = version.substring(0, version.lastIndexOf("."));
        }

        if (version.equals("0.0")) {
            return "skip";
        }

        return version;
    }

    private static int getQuarterByMonth(int month) {
        switch (month) {
            case 1:
            case 2:
            case 3:
                return 1;
            case 4:
            case 5:
            case 6:
                return 2;
            case 7:
            case 8:
            case 9:
                return 3;
            case 10:
            case 11:
            case 12:
                return 4;
        }

        return 0;
    }
}
