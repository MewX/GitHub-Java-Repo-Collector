package au.edu.uofa.sei.assignment1.collector.type;

import au.edu.uofa.sei.assignment1.collector.Constants;

public class RepoCommit extends BaseKeyRequestType {

    public RepoCommit() {
        super(RepoCommit.class.getSimpleName());
    }

    @Override
    public String constructParam(int page, String projectName) {
        StringBuilder query = new StringBuilder(); // the query without APP ID
        query.append(projectName).append("/commits?per_page=100");
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
