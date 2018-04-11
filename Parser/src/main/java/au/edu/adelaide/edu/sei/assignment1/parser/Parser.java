package au.edu.adelaide.edu.sei.assignment1.parser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;

public class Parser {
    public static void main(String[] args) throws SQLException {
        if (args.length != 4) {
            System.out.println("Usage: java -jar xxx.jar /path/to/repo commitTag dbName repoName");
            return;
        }

        // absolute path of project folder (root folder)
        File repoPath = new File(args[0]);
        String commitTag = args[1];
        String dbName = args[2]; // "dependencies.db"
        String repoName = args[3];
//        File inputFile = new File("/Users/nick/Documents/Github/MyAustralia2");

        // parser for pom.xml
        PomParser pomParser = new PomParser();
        // parser for build.gradle
        GradleParser gradleParser = new GradleParser();

        // store result to database
        Database db = new Database(dbName);

        System.out.println("Processing project: " + repoName);

        // get all "pom.xml" and "build.gradle" from sub-folder
        Collection<File> files = FileUtils.listFiles(repoPath, new IOFileFilter() {
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
                System.out.println("Found maven project.");
                pomParser.parsePomFile(file, db, repoName, commitTag);
            } else if (file.getName().equals("build.gradle")) {
                System.out.println("Found gradle project.");
                gradleParser.parseGradleFile(file, db, repoName, commitTag);
            }
        }

        System.out.println("Finish processing project: " + repoName);
        db.close();
    }
}
