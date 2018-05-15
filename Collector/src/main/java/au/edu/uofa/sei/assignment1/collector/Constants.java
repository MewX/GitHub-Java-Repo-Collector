package au.edu.uofa.sei.assignment1.collector;

public class Constants {
    public static final String HEADER_X_RATELIMIT_LIMIT = "X-RateLimit-Limit"; // 5000
    public static final String HEADER_X_RATELIMIT_REMAINING = "X-RateLimit-Remaining"; // 4999
    public static final String HEADER_X_RATELIMIT_RESET = "X-RateLimit-Reset"; // 1521981352

    public static final String APP_CLIENT_ID = "Iv1.7aad191fa36bbd25";
    public static final String APP_CLIENT_SECRET = "c9d413197d1e6b909086c9d9fd43a1a55f26a0ea";
    public static final String APP_ID_FOR_QUERY = "&client_id=" + APP_CLIENT_ID + "&client_secret=" + APP_CLIENT_SECRET;

    public static final String DB_NAME = "repo.db";
    public static final String TEMP_PATH = "temp/";
    public static final String BASE_PATH = "java-repos/";

    public static final String PROPERTY_USERREPO_SAVE_ID = "USERREPO_SAVE_ID"; // the last finished user repo index for collecting project commits
    public static final String PROPERTY_COMMIT_DETAIL_FETCHING_ID = "COMMIT_DETAIL_FETCHING_ID";
}
