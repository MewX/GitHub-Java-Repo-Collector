import java.io.File;

public class Parser {
    public static void main(String[] args) {
        // TODO modify file path
        File testFile = new File("test/pom.xml");
        File testGradle = new File("test/test2-build.gradle");

        // parser for pom.xml
        PomParser pomParser = new PomParser();
        // parser for build.gradle
        GradleParser gradleParser = new GradleParser();


        // call pom parser
        if (testGradle.getName().contains("pom.xml")) {
            pomParser.parsePomFile(testFile);
        // call gradle parser
        } else if (testGradle.getName().contains("build.gradle")) {
            gradleParser.parseGradleFile(testGradle);
        }
    }
}
