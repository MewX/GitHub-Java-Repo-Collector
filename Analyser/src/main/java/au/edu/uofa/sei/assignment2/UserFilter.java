package au.edu.uofa.sei.assignment2;

import au.edu.uofa.sei.assignment1.collector.db.Conn;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * get all users appears in the repoCommit
 */
public class UserFilter {
    public static void main(String[] args) throws SQLException, IOException {
        final Conn connection = new Conn("/Users/nick/Documents/学习资料/2018.S1/SEI/Assignment/Research 2/data/repo.db");
        Database db = new Database(connection);

        Set<String> userSet = new HashSet<>();

        ResultSet resultSet = db.getUserList();

        while (resultSet.next()) {
            String[] split = resultSet.getString("params").split("/");
            userSet.add(split[0]);
        }

        List<String> userList = new ArrayList<>(userSet);

        System.out.println(userList.size());

        Path out = Paths.get("output.txt");
        Files.write(out, userList);
    }
}
