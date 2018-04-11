package au.edu.adelaide.edu.sei.assignment1.parser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;

public class Parser {
    public static void main(String[] args) throws SQLException {
        if (args.length != 2) {
            System.out.println("Please specify a folder path to the project!");
            return;
        }

        // TODO modify file path
        // absolute path of project folder (root folder)
        File inputFile = new File(args[0]);
        String commitTag = args[1];
//        File inputFile = new File("/Users/nick/Documents/Github/MyAustralia2");

        // project name based on file path
        String projectName = inputFile.getName();

        // parser for pom.xml
        PomParser pomParser = new PomParser();
        // parser for build.gradle
        GradleParser gradleParser = new GradleParser();

        // store result to database
        Database db = new Database("dependencies.db");

        System.out.println("Processing project: " + projectName);

        // get all "pom.xml" and "build.gradle" from sub-folder
        Collection<File> files = FileUtils.listFiles(inputFile, new IOFileFilter() {
            public boolean accept(File file) {
                return !file.getName().contains(".git") &&
                        (file.getName().equals("pom.xml") || file.getName().equals("build.gradle"));
            }

            public boolean accept(File file, String s) {
                return true;
            }
        }, HiddenFileFilter.VISIBLE);

        for (File file : files) {
            if (file.getName().equals("pom.xml")) {
                pomParser.parsePomFile(file, db, projectName, commitTag);
            } else if (file.getName().equals("build.gradle")) {
                gradleParser.parseGradleFile(file, db, projectName, commitTag);
            }
        }

        System.out.println("Finish processing project: " + projectName);
        db.close();
    }
}
