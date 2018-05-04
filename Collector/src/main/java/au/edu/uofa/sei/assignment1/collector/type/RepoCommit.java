package au.edu.uofa.sei.assignment1.collector.type;

import au.edu.uofa.sei.assignment1.collector.Constants;

public class RepoCommit extends BaseKeyRequestType {

    public RepoCommit() {
        super(RepoCommit.class.getSimpleName());
    }

    /**
     * This API takes two parameters, the parameters should be comma-separated
     *
     * @param page               <=1 means first page
     * @param projectNameAndUserName comma separated string (projectName,userName)
     * @return the constructed params
     */
    @Override
    public String constructParam(int page, String projectNameAndUserName) {
        String[] p = projectNameAndUserName.split(",");

        StringBuilder query = new StringBuilder(); // the query without APP ID
        query.append(p[0]).append("/commits?author=").append(p[1]).append("&per_page=100");
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
