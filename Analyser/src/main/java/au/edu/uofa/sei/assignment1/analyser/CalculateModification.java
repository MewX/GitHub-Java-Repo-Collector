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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CalculateModification {
    public static void main(String[] args) throws SQLException, InterruptedException, IOException, ParseException {
        final Conn connection = new Conn("repo.db");
        RepoDB repoDB = new RepoDB(connection);

        List<String> userProjects = new ArrayList<>();
        List<String> organizationProjects = new ArrayList<>();

        // get all user project names
        ResultSet resultSet = repoDB.selectUserProjectName();

        while (resultSet.next()) {
            userProjects.add(resultSet.getString("project"));
        }

        // get all organization project names
        resultSet = repoDB.selectOrganizationProjectName();

        while (resultSet.next()) {
            organizationProjects.add(resultSet.getString("project"));
        }

//        int START = 0;
//        String userFilename = "user.csv";
//        // loop all user projects
//        for (int j = START; j < userProjects.size() && j < START + 200; j++) {
//            String project = userProjects.get(j);
//
//            List<Map<String, String>> projectDependency = new ArrayList<>();
//
//            System.err.println("Processing: " + project + " (" + (j + 1) + "/" + userProjects.size() + ")");
//
//            ResultSet commits = repoDB.selectCommitsOfProject(project);

            // get all commits of the project
//            while (commits.next()) {
//                String commit = commits.getString("commit_tag");
//
//                // get all dependencies with that commits
//                ResultSet dependencies = repoDB.selectDependency(commit);
//
//                Map<String, String> dependency = new HashMap<>();
//
//                // put all dependency to map
//                while (dependencies.next()) {
//                    dependency.put(dependencies.getString("artifact_id"), dependencies.getString("version"));
//                }
//
//                projectDependency.add(dependency);
//            }
//
//            System.out.println("Number of project version: " + projectDependency.size());
//
//            // process adding, deleting, and modification
//            int add = 0;
//            int delete = 0;
//            int modification = 0;
//            for (int i = 0; i < projectDependency.size() - 1; i++) {
//                Map<String, String> base = projectDependency.get(i);
//                Map<String, String> compare = projectDependency.get(i + 1);
//
//                // get adding number, if dependency in compare doesn't appear at base, +1
//                for (Map.Entry<String, String> entry : compare.entrySet()) {
//                    if (!base.containsKey(entry.getKey())) {
//                        add++;
//                    }
//                }
//
//                // get deleting number, if dependency in base doesn't appear at compare, +1
//                for (Map.Entry<String, String> entry : base.entrySet()) {
//                    if (!compare.containsKey(entry.getKey())) {
//                        delete++;
//                    }
//                }
//
//                // get modification, if version in compare different to base, +1
//                for (Map.Entry<String, String> entry : compare.entrySet()) {
//                    // only compare exist ones
//                    if (base.containsKey(entry.getKey())) {
//                        if (!base.get(entry.getKey()).equals(entry.getValue())) {
//                            modification++;
//                        }
//                    }
//                }
//            }
//
//            // output results
//            System.out.println("Adding: " + add);
//            System.out.println("Deleting: " + delete);
//            System.out.println("Modification: " + modification);
//
//            BufferedWriter writer = new BufferedWriter(new FileWriter(userFilename, true));
//            writer.append(project);
//            writer.append(",");
//            writer.append(String.valueOf(projectDependency.size()));
//            writer.append(",");
//            writer.append(String.valueOf(add));
//            writer.append(",");
//            writer.append(String.valueOf(delete));
//            writer.append(",");
//            writer.append(String.valueOf(modification));
//            writer.append("\n");
//
//            writer.close();

            // get earliest commit time
//            commits.first();
//
//            String earliestCommit = "";
//            String latestCommit = "";
//
//            // get earliest and latest commit time
//            while (commits.next()) {
//                if (commits.isFirst()) {
//                    earliestCommit = commits.getString("time");
//                    continue;
//                }
//
//                latestCommit = commits.getString("time");
//            }
//
//            if (earliestCommit.equals("")) {
//                continue;
//            }
//
//            if (latestCommit.equals("")) {
//                latestCommit = earliestCommit;
//            }
//
//            // calculate the difference in weeks
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//            Date earliest = simpleDateFormat.parse(earliestCommit);
//            Date latest = simpleDateFormat.parse(latestCommit);
//
//            long diffInMillies = Math.abs(latest.getTime() - earliest.getTime());
//            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
//
//            if (diff == 0) {
//                diff = 7;
//            }
//
//            System.out.println("first: " + earliestCommit);
//            System.out.println("last: " + latestCommit);
//            System.out.println("duration (days): " + diff);
//
//            BufferedWriter writer = new BufferedWriter(new FileWriter(userFilename, true));
//            writer.append(project);
//            writer.append(",");
//            writer.append(earliestCommit);
//            writer.append(",");
//            writer.append(latestCommit);
//            writer.append(",");
//            writer.append(String.valueOf((int)diff));
//            writer.append("\n");
//
//            writer.close();
//
//            Thread.sleep(3);
//        }

        // loop all organization projects
        int START = 100;
        String organizationFilename = "organization.csv";
        for (int j = START; j < organizationProjects.size() && j < START + 200; j++) {
            String project = organizationProjects.get(j);

            List<Map<String, String>> projectDependency = new ArrayList<>();

            System.err.println("Processing: " + project + " (" + (j+1) + "/" + organizationProjects.size() + ")");

            ResultSet commits = repoDB.selectCommitsOfProject(project);

            // get all commits of the project
//            while (commits.next()) {
//                String commit = commits.getString("commit_tag");
//
//                // get all dependencies with that commits
//                ResultSet dependencies = repoDB.selectDependency(commit);
//
//                Map<String, String> dependency = new HashMap<>();
//
//                // put all dependency to map
//                while (dependencies.next()) {
//                    dependency.put(dependencies.getString("artifact_id"), dependencies.getString("version"));
//                }
//
//                projectDependency.add(dependency);
//            }
//
//            System.out.println("Number of project version: " + projectDependency.size());
//
//            // process adding, deleting, and modification
//            int add = 0;
//            int delete = 0;
//            int modification = 0;
//            for (int i = 0; i < projectDependency.size() - 1; i++) {
//                Map<String, String> base = projectDependency.get(i);
//                Map<String, String> compare = projectDependency.get(i + 1);
//
//                // get adding number, if dependency in compare doesn't appear at base, +1
//                for (Map.Entry<String, String> entry : compare.entrySet()) {
//                    if (!base.containsKey(entry.getKey())) {
//                        add++;
//                    }
//                }
//
//                // get deleting number, if dependency in base doesn't appear at compare, +1
//                for (Map.Entry<String, String> entry : base.entrySet()) {
//                    if (!compare.containsKey(entry.getKey())) {
//                        delete++;
//                    }
//                }
//
//                // get modification, if version in compare different to base, +1
//                for (Map.Entry<String, String> entry : compare.entrySet()) {
//                    // only compare exist ones
//                    if (base.containsKey(entry.getKey())) {
//                        if (!base.get(entry.getKey()).equals(entry.getValue())) {
//                            modification++;
//                        }
//                    }
//                }
//            }
//
//            // output results
//            System.out.println("Adding: " + add);
//            System.out.println("Deleting: " + delete);
//            System.out.println("Modification: " + modification);
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter(organizationFilename, true));
//        writer.append(project);
//        writer.append(",");
//        writer.append(String.valueOf(projectDependency.size()));
//        writer.append(",");
//        writer.append(String.valueOf(add));
//        writer.append(",");
//        writer.append(String.valueOf(delete));
//        writer.append(",");
//        writer.append(String.valueOf(modification));
//        writer.append("\n");
//
//        writer.close();


            String earliestCommit = "";
            String latestCommit = "";

            // get earliest and latest commit time
            while (commits.next()) {
                if (commits.isFirst()) {
                    earliestCommit = commits.getString("time");
                    continue;
                }

                latestCommit = commits.getString("time");
            }

            if (earliestCommit.equals("")) {
                continue;
            }

            if (latestCommit.equals("")) {
                latestCommit = earliestCommit;
            }

            // calculate the difference in weeks
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date earliest = simpleDateFormat.parse(earliestCommit);
            Date latest = simpleDateFormat.parse(latestCommit);

            long diffInMillies = Math.abs(latest.getTime() - earliest.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            if (diff == 0) {
                diff = 7;
            }

            System.out.println("first: " + earliestCommit);
            System.out.println("last: " + latestCommit);
            System.out.println("duration (days): " + diff);

            BufferedWriter writer = new BufferedWriter(new FileWriter(organizationFilename, true));
            writer.append(project);
            writer.append(",");
            writer.append(earliestCommit);
            writer.append(",");
            writer.append(latestCommit);
            writer.append(",");
            writer.append(String.valueOf((int)diff));
            writer.append("\n");

            writer.close();

            Thread.sleep(3);
        }
    }
}
