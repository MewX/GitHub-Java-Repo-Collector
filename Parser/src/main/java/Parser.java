import java.io.File;

public class Parser {
    public static void main(String[] args) {
        // TODO modify file path
//        File inputFile = new File("test/test2-pom.xml");
//        File inputFile = new File("test/test3-build.gradle");
        // absolute path of pom or gradle file
        File inputFile = new File(args[0]);

        // parser for pom.xml
        PomParser pomParser = new PomParser();
        // parser for build.gradle
        GradleParser gradleParser = new GradleParser();

        // store result to database
        Database db = new Database("dependencies.db");

        // call pom parser
        if (inputFile.getName().contains("pom.xml")) {
            pomParser.parsePomFile(inputFile, db);
        // call gradle parser
        } else if (inputFile.getName().contains("build.gradle")) {
            gradleParser.parseGradleFile(inputFile, db);
        }
    }
}
