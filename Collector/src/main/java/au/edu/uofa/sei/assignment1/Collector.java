package au.edu.uofa.sei.assignment1;

import java.util.Map;

public class Collector {
    public static void main(String[] args) {
        // curl -iH 'User-Agent: UofA SEI 2018 Project' https://api.github.com -u mseopt:mseoptpassword2018
        // curl -i 'https://api.github.com/users/whatever?client_id=Iv1.ad6b73b7b61c26f3&client_secret=9ac009128eb89e8c2b0f9d39fddd0378d3dfbdc0'
        // curl 'https://api.github.com/user/repos?page=2&per_page=100'
        // if exceeded: 403 Forbidden -> exit directly

        // because github allows searching for 1000 results only,
        // therefore, using star size limit can expand the searching results:
        // https://api.github.com/search/repositories?q=language:java+stars:%3C1534&sort=stars&order=desc&per_page=100&page=10

        Map<String, String> test = LightNetwork.lightHttpRequest("https://api.github.com/search/repositories?q=language:java&sort=stars&order=desc" + Constants.APP_ID_FOR_QUERY);
        System.out.println("limit: " + test.getOrDefault(Constants.HEADER_X_RATELIMIT_LIMIT, "0"));
        System.out.println("remaining: " + test.getOrDefault(Constants.HEADER_X_RATELIMIT_REMAINING, "0"));
        System.out.println("reset: " + test.getOrDefault(Constants.HEADER_X_RATELIMIT_RESET, "" + (System.currentTimeMillis() / 1000)));
        System.out.println("current: " + (System.currentTimeMillis() / 1000));

        // for each repo, if first level and second level dir don't contain pom.xml or build.gradle; skip it
        System.out.println("sample output");
    }
}
