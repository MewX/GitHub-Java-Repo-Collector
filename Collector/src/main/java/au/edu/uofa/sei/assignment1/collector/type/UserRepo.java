package au.edu.uofa.sei.assignment1.collector.type;

import au.edu.uofa.sei.assignment1.collector.Constants;

public class UserRepo extends BaseKeyRequestType {

    public UserRepo() {
        super(UserRepo.class.getSimpleName());
    }

    @Override
    public String constructParam(int page, String userName) {
        StringBuilder query = new StringBuilder(); // the query without APP ID
        query.append(userName).append("/repos?per_page=100");
        if (page > 1) {
            // not first page
            query.append("&page=").append(page);
        }
        return query.toString();
    }

    @Override
    public String constructRequestUrl(String param) {
        return "https://api.github.com/users/" + param + Constants.APP_ID_FOR_QUERY;
    }

}
