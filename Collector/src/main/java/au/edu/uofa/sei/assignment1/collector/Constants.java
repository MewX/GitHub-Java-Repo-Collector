package au.edu.uofa.sei.assignment1.collector;

public class Constants {
    public static final String HEADER_X_RATELIMIT_LIMIT = "X-RateLimit-Limit"; // 5000
    public static final String HEADER_X_RATELIMIT_REMAINING = "X-RateLimit-Remaining"; // 4999
    public static final String HEADER_X_RATELIMIT_RESET = "X-RateLimit-Reset"; // 1521981352

    public static final String APP_CLIENT_ID = "Iv1.ad6b73b7b61c26f3";
    public static final String APP_CLIENT_SECRET = "9ac009128eb89e8c2b0f9d39fddd0378d3dfbdc0";
    public static final String APP_ID_FOR_QUERY = "&client_id=" + APP_CLIENT_ID + "&client_secret=" + APP_CLIENT_SECRET;

    public static final String DB_NAME = "repo.db";
    public static final String TEMP_PATH = "temp/";
    public static final String BASE_PATH = "java-repos/";

    public static final String PROPERTY_USERREPO_SAVE_ID = "USERREPO_SAVE_ID"; // the last finished user repo index for collecting project commits
}
