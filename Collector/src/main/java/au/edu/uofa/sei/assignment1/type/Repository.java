package au.edu.uofa.sei.assignment1.type;

import au.edu.uofa.sei.assignment1.Constants;
import au.edu.uofa.sei.assignment1.LightNetwork;
import au.edu.uofa.sei.assignment1.db.QueryDb;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Repository {
    public static String TYPE = Repository.class.getName();

    //"https://api.github.com/search/repositories?q=language:java&sort=stars&order=desc" + au.edu.uofa.sei.assignment1.Constants.APP_ID_FOR_QUERY);
    private static String constructQuerySection(int maxStarWanted) {
        return "language:java" + (maxStarWanted > 0 ? "+stars:<" + maxStarWanted : "");
    }

    /**
     * make request and save to database automatically
     * @param page <=1 means first page
     * @param maxStarWanted <= 0 means no upper limit
     * @return request content
     */
    public static Map<String, String> makeRequest(int page, int maxStarWanted, Map<String, String> prevReq, QueryDb queryDb) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("q=").append(constructQuerySection(maxStarWanted)).append("&sort=stars&order=desc&per_page=100");
        if (page > 1) {
            // not first page
            sb.append("&page=").append(page);
        }
        String url = "https://api.github.com/search/repositories?" + sb.toString() + Constants.APP_ID_FOR_QUERY;

        if (queryDb.checkExistance(TYPE, sb.toString())) {
            System.err.println("Found existing record, skipped: " + sb.toString());
            return prevReq;
        }

        // request
        String result = null;
        Map<String, String> ret = null;
        while (result == null || result.trim().length() == 0 || result.trim().equals("null")) {
            ret = LightNetwork.lightHttpRequest(prevReq, url);
            result = ret.get(LightNetwork.HEADER_CONTENT);
        }
        queryDb.insert(TYPE, sb.toString(), result);
        return ret;
    }

    public static void collectingRepos(QueryDb db) throws SQLException {
//        int maxStar = 0; // by default from 0
        int maxStar = 0; // if interrupted, use this line

        for (int nums = 0; nums < 4000; nums += 1000) {
            Map<String, String> prev = null;
            for (int i = 1; i <= 10; i++) {
                System.err.format("Getting list: p%d(%d+)\n", i, nums);
                prev = makeRequest(i, maxStar, prev, db);
            }

            // update maxStar
            Pattern p = Pattern.compile("stargazers_count\"[^\\d]*(\\d+?),");
            Matcher m = p.matcher(prev.get(LightNetwork.HEADER_CONTENT));
            while (m.find()) {
                int temp = Integer.valueOf(m.group(1));
                if (maxStar == 0 || temp < maxStar) maxStar = temp;
            }
        }
    }
}
