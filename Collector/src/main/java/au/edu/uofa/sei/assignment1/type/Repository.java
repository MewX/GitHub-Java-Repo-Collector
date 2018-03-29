package au.edu.uofa.sei.assignment1.type;

import au.edu.uofa.sei.assignment1.Constants;
import au.edu.uofa.sei.assignment1.LightNetwork;
import au.edu.uofa.sei.assignment1.QueryDb;

import java.sql.SQLException;
import java.util.Map;

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

        // request
        LightNetwork.waitUntilRefresh(prevReq);
        Map<String, String> ret = LightNetwork.lightHttpRequest(url);
        queryDb.insert(TYPE, sb.toString(), ret.get(LightNetwork.HEADER_CONTENT));

        return ret;
    }
}
