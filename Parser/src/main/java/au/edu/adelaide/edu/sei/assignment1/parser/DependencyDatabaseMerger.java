package au.edu.adelaide.edu.sei.assignment1.parser;

import java.sql.SQLException;
import java.util.List;

public class DependencyDatabaseMerger {
    public static void main(String[] args) throws SQLException {
        if (args.length != 2 && args.length != 3) {
            System.err.println("usage: java -jar xxx.jar totalNum targetDbName <true/false to ignore last one>");
            System.exit(0);
        }

        final int TOTAL_NUMBER = Integer.valueOf(args[0]);
        final String TARGET_DB_NAME = args[1];
        final boolean IGNORE_LAST_ONE = args.length >= 3 && args[2].equalsIgnoreCase("true");

        Database db = new Database(TARGET_DB_NAME);
        for (int fragmentId = 1; fragmentId <= TOTAL_NUMBER; fragmentId ++) {
            final String dependencyDbName = "dep" + fragmentId + "of" + TOTAL_NUMBER + ".db";
            System.err.println("Proceeding " + dependencyDbName);

            Database temp = new Database(dependencyDbName);
            List<String> projectNames = temp.selectProjectNames();
            if (IGNORE_LAST_ONE) projectNames.remove(projectNames.size() - 1); // remove last one

            for (String name : projectNames) {
                List<Database.Dependency> dependencies = temp.selectDependencies(name);
                // save to the new database
                for (Database.Dependency dep : dependencies) {
                    db.insert(dep.project, dep.commitTag, dep.groupId, dep.artifactid, dep.version);
                }
            }
            temp.close();
        }
        db.close();
    }
}
