package au.edu.uofa.sei.assignment1.collector.type;

import au.edu.uofa.sei.assignment1.collector.Constants;
import au.edu.uofa.sei.assignment1.collector.LightNetwork;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The request used for getting contributors from a repository
 */
public class Contributor {
    public static String TYPE = Contributor.class.getSimpleName();

    public static String constructParam(int page, String projectName) {
        StringBuilder query = new StringBuilder(); // the query without APP ID
        query.append(projectName).append("/contributors?per_page=100");
        if (page > 1) {
            // not first page
            query.append("&page=").append(page);
        }
        return query.toString();
    }

    /**
     * make request and save to database automatically
     *
     * @param page        <=1 means first page
     * @param projectName owner/projectName
     * @return request content
     */
    public static Map<String, String> makeRequest(int page, String projectName, Map<String, String> prevReq, QueryDb queryDb) throws SQLException {
        final String query = constructParam(page, projectName);
        final String url = "https://api.github.com/repos/" + query + Constants.APP_ID_FOR_QUERY;

        if (queryDb.checkExistence(TYPE, query)) {
            System.err.println("Found existing record, skipped: " + query);
            return prevReq;
        }

        // request
        String result = null;
        Map<String, String> ret = null;
        while (result == null || result.trim().length() == 0 || result.trim().equals("null")) {
            ret = LightNetwork.lightHttpRequest(prevReq, url);
            result = ret.get(LightNetwork.HEADER_CONTENT);
        }
        queryDb.insert(TYPE, query, result);
        return ret;
    }

    public static Map<String, String> collectingContributors(String projectName, Map<String, String> prevReq, QueryDb db) throws SQLException {
        final Pattern lastPattern = Pattern.compile("<.+?[^_]page=(\\d+)>;\\s*rel=\"last\"");

        // do request for the first one
        int page = 1;
        int maxPage = page;
        Map<String, String> prev = makeRequest(page, projectName, prevReq, db);
        Matcher matcher = lastPattern.matcher(prev.get("Link"));
        if (matcher.find()) {
            maxPage = Integer.valueOf(matcher.group(1));
        } else {
            System.err.println("INFO - Only one page contributor found");
        }

        // loop through every page
        for (page += 1; page < maxPage; page++) {
            prev = makeRequest(page, projectName, prev, db);
        }
        return prev;
    }

    public static boolean checkProjectExistance(String projectName, QueryDb db) {
        try {
            return db.checkExistence(TYPE, constructParam(1, projectName));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
