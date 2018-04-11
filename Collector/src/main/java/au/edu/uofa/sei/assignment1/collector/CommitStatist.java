package au.edu.uofa.sei.assignment1.collector;

import au.edu.adelaide.edu.sei.assignment1.parser.Parser;
import au.edu.uofa.sei.assignment1.collector.db.CommitDb;
import au.edu.uofa.sei.assignment1.collector.db.Conn;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommitStatist {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static void main(String[] args) throws SQLException, IOException, GitAPIException {
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "INFO");
        PropertyConfigurator.configure(prop);

        int noOfGroups, groupId;
        noOfGroups = groupId = 0;

        // test jgit switching branch
        if (args.length == 2 || args.length == 3) {
            switch (args[0]) {
                case "d":
                    // e.g. java -jar xxx.jar d path/to/.git 9f43ab...
                    System.err.format("detaching to %s from %s\n", args[1], args[2]);
                    detachBranch(args[1], args[2]);
                    break;
                case "c":
                    // e.g. java -jar xxx.jar c path/to/.git
                    System.err.format("reattaching head: %s\n", args[1]);
                    reattachMasterBranch(args[1]);
                    break;
                case "r":
                    // e.g. java -jar xxx.jar r noOfGroups groupId(0-noOfGroups)
                    noOfGroups = Integer.valueOf(args[1]);
                    groupId = Integer.valueOf(args[2]);
                    break;
                default:
                    System.err.println("usage: java -jar xxx.jar r noOfGroups groupId(0-noOfGroups)");
                    return;
            }
        } else {
            System.err.println("Invalid input args:");
            for (String arg : args) {
                System.out.println(arg);
            }
            return;
        }

        Conn c = new Conn(Constants.DB_NAME);
        CommitDb commitDb = new CommitDb(c);

        // get all list from database
        ArrayList<String> repoNames = LogWalker.getRepos(c); // the order does not change

        // select
        final String dependencyDbName = "dep" + (groupId + 1) + "of" + noOfGroups + ".db";
        final int sizeOfEachGroup = repoNames.size() / noOfGroups;
        final int UPPER_LIMIT = groupId + 1 != noOfGroups ? (sizeOfEachGroup * (groupId + 1)) : repoNames.size();
        System.err.format("Repos: %d/%d out of %d\n", groupId * sizeOfEachGroup, UPPER_LIMIT, repoNames.size());
        for (int i = sizeOfEachGroup * groupId; i < UPPER_LIMIT; i++) {
            final String projectName = repoNames.get(i);
            System.err.println("Working on repo: " + projectName);
            List<CommitDb.Commit> commits = commitDb.resultToCommit(commitDb.select(projectName)); // sorted by date
            final String pathToRepo = Constants.BASE_PATH + projectName;
            final String pathToDotGit = pathToRepo + "/.git";

            Calendar baseCalendar = Calendar.getInstance();
            baseCalendar.setTimeInMillis(commits.get(0).time.getTime());
            baseCalendar.set(Calendar.HOUR, 0); // initialized as 0
            baseCalendar.set(Calendar.MINUTE, 0);
            baseCalendar.set(Calendar.SECOND, 0);
            System.err.println("    First week time is selected: " + DATE_FORMAT.format(baseCalendar.getTime()));

            // pick date every
            int idxCommit = 0;
            while (idxCommit < commits.size()) {
                final CommitDb.Commit commit = commits.get(idxCommit);
                final Date date = new Date(commit.time.getTime());

                if (!baseCalendar.after(date)) {
                    // commit date is equal to or after base date
                    baseCalendar.add(Calendar.DATE, 7); // add one week
                    if (baseCalendar.after(date)) {
                        // good, this is what I want, and I will use this commit
                        System.err.println("    Selected commit: " + commit.msg + " - " + DATE_FORMAT.format(date));

                        // reset to master branch before detach the master branch into a specific commit hash
                        reattachMasterBranch(pathToDotGit);
                        detachBranch(pathToDotGit, commit.msg);

                        // run dependency walker
                        Parser.main(new String[]{
                                pathToRepo,
                                commit.msg,
                                dependencyDbName,
                                projectName
                        });
                    }
                }
                idxCommit++;
            }
        }

        c.close();
    }

    private static void detachBranch(String pathToDotGit, String hash) throws IOException, GitAPIException {
        Git git = LogWalker.openExistingRepo(pathToDotGit);
        git.checkout()
                .setCreateBranch(false)
                .setName(hash)
                .call();
        git.close();
    }

    private static void reattachMasterBranch(String pathToDotGit) throws IOException, GitAPIException {
        Git git = LogWalker.openExistingRepo(pathToDotGit);
        String branchName = null;
        for (Ref b : git.branchList().call()) {
            System.err.println("found branch: " + b.getName());
            if (b.getName().contains("refs/heads")) {
                branchName = b.getName();
                break;
            }
        }
        git.checkout()
                .setCreateBranch(false)
                .setName(branchName) // use the first default branch name
                .call();
        git.close();
    }

}
