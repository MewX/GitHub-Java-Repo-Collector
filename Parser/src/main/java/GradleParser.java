import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GradleParser {
    public void parseGradleFile(File file, Database db, String projectName, String commitTag) {
        try {
            boolean hasDependency = false;

            // read file
            List<String> lines = FileUtils.readLines(file, "UTF-8");

            for (int i = 0; i < lines.size(); i++) {
                // look for dependencies
                if (lines.get(i).trim().startsWith("dependencies {")) {
                    // move to next line
                    i++;

                    while (!lines.get(i).startsWith("}")) {
                        // skip empty line or comment line
                        if (lines.get(i).equals("") || lines.get(i).trim().startsWith("//")) {
                            i++;
                            continue;
                        }

                        // string that contains dependency information
                        String[] dependency = StringUtils.substringsBetween(lines.get(i), "\"", "\"");

                        if (dependency == null) {
                            dependency = StringUtils.substringsBetween(lines.get(i), "\'", "\'");

                            // didn't find dependency info, move to next line
                            if (dependency == null) {
                                i++;
                                continue;
                            }
                        }

                        String[] dependencyInfo = dependency[0].split(":");

                        if (dependencyInfo.length == 3) {
                            hasDependency = true;

                            saveDependency(dependencyInfo, db, projectName, commitTag);
                        }

                        //move to next line
                        i++;
                    }
                }
            }

            // no dependency found
            if (!hasDependency) {
                // check if project already exist
                if (!db.checkProjectExistance(projectName)) {
                    db.insert(projectName, null,"no dependency", "no dependency", "no dependency");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDependency(String[] dependency, Database db, String projectName, String commitTag) {
//        System.out.println("GroupId: " + dependencyInfo[0]);
//        System.out.println("artifactId: " + dependencyInfo[1]);
//        System.out.println("version: " + dependencyInfo[2]);
//        System.out.print("\n");

        String groupId = dependency[0];
        String artifactId = dependency[1];
        String version = dependency[2];

//        if (!db.checkExistance(projectName, groupId, artifactId, version)) {
            db.insert(projectName, commitTag, groupId, artifactId, version);
//        }
    }
}
