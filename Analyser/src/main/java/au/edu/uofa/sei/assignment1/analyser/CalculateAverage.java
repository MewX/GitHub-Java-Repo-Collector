package au.edu.uofa.sei.assignment1.analyser;

import au.edu.uofa.sei.assignment1.analyser.db.RepoDB;
import au.edu.uofa.sei.assignment1.collector.db.Conn;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CalculateAverage {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        final Conn connection = new Conn("repo.db");
        RepoDB repoDB = new RepoDB(connection);

        String userFilename = "user-average.csv";
        String organizationFilename = "organization-average.csv";
        String totalFilename = "total-average.csv";

//        BufferedWriter userWriter = new BufferedWriter(new FileWriter(userFilename, true));
//        BufferedWriter organizationWriter = new BufferedWriter(new FileWriter(organizationFilename, true));
        BufferedWriter totalWriter = new BufferedWriter(new FileWriter(totalFilename, true));


        for (int i = 2008; i < 2018; i++) {
            // get user data
            // Q1
//            System.err.println("Processing User " + i + " Q1");
//            ResultSet resultSet = repoDB.selectAverage("User", i + "-01-01", i + "-03-31");
//
//            System.out.println("User " + i + " Q1: " + resultSet.getString("average"));
//
//            userWriter.append(String.valueOf(i));
//            userWriter.append(" Q1, ");
//            userWriter.append(String.valueOf(resultSet.getString("average")));
//            userWriter.append("\n");
//
//            Thread.sleep(2);
//
//            // Q2
//            System.err.println("Processing User " + i + " Q2");
//            resultSet = repoDB.selectAverage("User", i + "-04-01", i + "-06-30");
//
//            System.out.println("User " + i + " Q2: " + resultSet.getString("average"));
//
//            userWriter.append(String.valueOf(i));
//            userWriter.append(" Q2, ");
//            userWriter.append(String.valueOf(resultSet.getString("average")));
//            userWriter.append("\n");
//
//            Thread.sleep(2);
//
//            // Q3
//            System.err.println("Processing User " + i + " Q3");
//            resultSet = repoDB.selectAverage("User", i + "-07-01", i + "-09-30");
//
//            System.out.println("User " + i + " Q3: " + resultSet.getString("average"));
//
//            userWriter.append(String.valueOf(i));
//            userWriter.append(" Q3, ");
//            userWriter.append(String.valueOf(resultSet.getString("average")));
//            userWriter.append("\n");
//
//            Thread.sleep(2);
//
//            // Q4
//            System.err.println("Processing User " + i + " Q4");
//            resultSet = repoDB.selectAverage("User", i + "-10-01", i + "-12-31");
//
//            System.out.println("User " + i + " Q4: " + resultSet.getString("average"));
//
//            userWriter.append(String.valueOf(i));
//            userWriter.append(" Q4, ");
//            userWriter.append(String.valueOf(resultSet.getString("average")));
//            userWriter.append("\n");
//
//            Thread.sleep(2);
//
//            // get organization data
//            // Q1
//            System.err.println("Processing Organization " + i + " Q1");
//            resultSet = repoDB.selectAverage("Organization", i + "-01-01", i + "-03-31");
//
//            System.out.println("Organization " + i + " Q1: " + resultSet.getString("average"));
//
//            organizationWriter.append(String.valueOf(i));
//            organizationWriter.append(" Q1, ");
//            organizationWriter.append(String.valueOf(resultSet.getString("average")));
//            organizationWriter.append("\n");
//
//            Thread.sleep(2);
//
//            // Q2
//            System.err.println("Processing Organization " + i + " Q2");
//            resultSet = repoDB.selectAverage("Organization", i + "-04-01", i + "-06-30");
//
//            System.out.println("Organization " + i + " Q2: " + resultSet.getString("average"));
//
//            organizationWriter.append(String.valueOf(i));
//            organizationWriter.append(" Q2, ");
//            organizationWriter.append(String.valueOf(resultSet.getString("average")));
//            organizationWriter.append("\n");
//
//            Thread.sleep(2);
//
//            // Q3
//            System.err.println("Processing Organization " + i + " Q3");
//            resultSet = repoDB.selectAverage("Organization", i + "-07-01", i + "-09-30");
//
//            System.out.println("Organization " + i + " Q3: " + resultSet.getString("average"));
//
//            organizationWriter.append(String.valueOf(i));
//            organizationWriter.append(" Q3, ");
//            organizationWriter.append(String.valueOf(resultSet.getString("average")));
//            organizationWriter.append("\n");
//
//            Thread.sleep(2);
//
//            // Q4
//            System.err.println("Processing Organization " + i + " Q4");
//            resultSet = repoDB.selectAverage("Organization", i + "-10-01", i + "-12-31");
//
//            System.out.println("Organization " + i + " Q4: " + resultSet.getString("average"));
//
//            organizationWriter.append(String.valueOf(i));
//            organizationWriter.append(" Q4, ");
//            organizationWriter.append(String.valueOf(resultSet.getString("average")));
//            organizationWriter.append("\n");
//
//            Thread.sleep(2);

            // get organization data
            // Q1
            System.err.println("Processing " + i + " Q1");
            ResultSet resultSet = repoDB.selectAverage("Organization", i + "-01-01", i + "-03-31");

            System.out.println(i + " Q1: " + resultSet.getString("average"));

            totalWriter.append(String.valueOf(i));
            totalWriter.append(" Q1, ");
            totalWriter.append(String.valueOf(resultSet.getString("average")));
            totalWriter.append("\n");

            Thread.sleep(2);

            // Q2
            System.err.println("Processing " + i + " Q2");
            resultSet = repoDB.selectAverage("Organization", i + "-04-01", i + "-06-30");

            System.out.println(i + " Q2: " + resultSet.getString("average"));

            totalWriter.append(String.valueOf(i));
            totalWriter.append(" Q2, ");
            totalWriter.append(String.valueOf(resultSet.getString("average")));
            totalWriter.append("\n");

            Thread.sleep(2);

            // Q3
            System.err.println("Processing " + i + " Q3");
            resultSet = repoDB.selectAverage("Organization", i + "-07-01", i + "-09-30");

            System.out.println(i + " Q3: " + resultSet.getString("average"));

            totalWriter.append(String.valueOf(i));
            totalWriter.append(" Q3, ");
            totalWriter.append(String.valueOf(resultSet.getString("average")));
            totalWriter.append("\n");

            Thread.sleep(2);

            // Q4
            System.err.println("Processing " + i + " Q4");
            resultSet = repoDB.selectAverage("Organization", i + "-10-01", i + "-12-31");

            System.out.println(i + " Q4: " + resultSet.getString("average"));

            totalWriter.append(String.valueOf(i));
            totalWriter.append(" Q4, ");
            totalWriter.append(String.valueOf(resultSet.getString("average")));
            totalWriter.append("\n");

            Thread.sleep(2);

//            userWriter.flush();
//            organizationWriter.flush();
            totalWriter.flush();
        }

//        userWriter.close();
//        organizationWriter.close();
        totalWriter.close();
    }
}
