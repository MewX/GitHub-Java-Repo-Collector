package au.edu.uofa.sei.assignment1.collector;

import au.edu.uofa.sei.assignment1.collector.db.CommitDb;
import au.edu.uofa.sei.assignment1.collector.db.Conn;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class CommitStatist {
    public static void main(String[] args) throws SQLException, IOException, GitAPIException {
        Properties prop = new Properties();
        prop.setProperty("log4j.rootLogger", "INFO");
        PropertyConfigurator.configure(prop);

        // test jgit switching branch
        if (args.length == 2 || args.length == 3) {
            if (args[0].equals("d")) {
                System.err.format("detaching to %s from %s\n", args[1], args[2]);
                detachBranch(args[1], args[2]);
            } else if (args[0].equals("c")) {
                System.err.format("reattaching head: %s\n", args[1]);
                reattachMasterBranch(args[1]);
            }
        }

        Conn c = new Conn(Constants.DB_NAME);
        CommitDb commitDb = new CommitDb(c);

        // get all list from database
        ArrayList<String> repoNames = LogWalker.getRepos(c); // the order does not change

        // select


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
