package au.edu.uofa.sei.assignment1.collector.type;

import au.edu.uofa.sei.assignment1.collector.Constants;

public class RepoCommitDetail extends BaseKeyRequestType {

    public RepoCommitDetail() {
        super(RepoCommitDetail.class.getSimpleName());
    }

    /**
     * This API takes two parameters, the parameters should be comma-separated
     *
     * @param page               <=1 means first page
     * @param projectNameAndHash comma separated string (projectName,commitHash)
     * @return the constructed params
     */
    @Override
    public String constructParam(int page, String projectNameAndHash) {
        final String[] p = projectNameAndHash.split(",");

        StringBuilder query = new StringBuilder(); // the query without APP ID
        query.append(p[0]).append("/commits/").append(p[1]).append("?per_page=100");
        if (page > 1) {
            // not first page
            query.append("&page=").append(page);
        }
        return query.toString();
    }

    @Override
    public String constructRequestUrl(String param) {
        return "https://api.github.com/repos/" + param + Constants.APP_ID_FOR_QUERY;
    }

}
