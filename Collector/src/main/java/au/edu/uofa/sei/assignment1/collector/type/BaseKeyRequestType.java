package au.edu.uofa.sei.assignment1.collector.type;

import au.edu.uofa.sei.assignment1.collector.LightNetwork;
import au.edu.uofa.sei.assignment1.collector.db.QueryDb;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The base key-based request type
 */
public abstract class BaseKeyRequestType {
    final public String TYPE;

    public BaseKeyRequestType(String type) {
        TYPE = type;
    }

    /**
     * construct param used for query data from database
     *
     * @param page <=1 means first page
     * @param key  query key
     * @return request content
     */
    abstract public String constructParam(int page, String key);

    /**
     * construct full url based on constructed param
     *
     * @param param the param constructed from constructParam() function
     * @return the full url
     */
    abstract public String constructRequestUrl(String param);

    /**
     * make request and save to database automatically
     *
     * @param page    page number
     * @param key     query key
     * @param prevReq previous response header used for controlling crawling speed
     * @param queryDb the query table
     * @return current response header
     */
    public Map<String, String> makeRequest(int page, String key, Map<String, String> prevReq, QueryDb queryDb) throws SQLException {
        final String query = constructParam(page, key);
        final String url = constructRequestUrl(query);

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

    /**
     * Collect all data as a loop
     *
     * @param key     the query key
     * @param prevReq the previous response
     * @param db      table handler
     * @return current response header
     */
    public Map<String, String> collect(String key, Map<String, String> prevReq, QueryDb db) throws SQLException {
        final Pattern lastPattern = Pattern.compile("<.+?[^_]page=(\\d+)>;\\s*rel=\"last\"");
        final String LINK_KEY = "Link";

        // do request for the first one
        int page = 1;
        int maxPage = page;
        Map<String, String> prev = makeRequest(page, key, prevReq, db);
        if (prev != null && prev.containsKey(LINK_KEY)) {
            Matcher matcher = lastPattern.matcher(prev.get(LINK_KEY));
            if (matcher.find()) {
                maxPage = Integer.valueOf(matcher.group(1));
            } else {
                System.err.println("INFO - Only one page found in " + TYPE + " request");
            }

            // loop through every page
            for (page += 1; page < maxPage; page++) {
                prev = makeRequest(page, key, prev, db);
            }
        }
        return prev;
    }

    /**
     * check whether a param exists
     *
     * @param key the query key
     * @param db  the table handler
     * @return true - existing; otherwise false
     */
    public boolean checkExistence(String key, QueryDb db) {
        try {
            return db.checkExistence(TYPE, constructParam(1, key));
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
