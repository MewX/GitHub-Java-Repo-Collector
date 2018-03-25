import java.util.Map;

public class Collector {
    public static void main(String[] args) {
        // curl -iH 'User-Agent: UofA SEI 2018 Project' https://api.github.com -u mseopt:mseoptpassword2018
        // curl -i 'https://api.github.com/users/whatever?client_id=Iv1.ad6b73b7b61c26f3&client_secret=9ac009128eb89e8c2b0f9d39fddd0378d3dfbdc0'
        // curl -iH 'User-Agent: UofA SEI 2018 Project' 'https://api.github.com/search/repositories?q=language:java&sort=stars&order=desc&client_id=Iv1.ad6b73b7b61c26f3&client_secret=9ac009128eb89e8c2b0f9d39fddd0378d3dfbdc0'
        // curl 'https://api.github.com/user/repos?page=2&per_page=100'
        // if exceeded: 403 Forbidden -> exit directly

        Map<String, String> test = LightNetwork.lightHttpRequest("https://api.github.com/search/repositories?q=language:java&sort=stars&order=desc&client_id=Iv1.ad6b73b7b61c26f3&client_secret=9ac009128eb89e8c2b0f9d39fddd0378d3dfbdc0");
        System.out.println("limit: " + test.get(Constants.HEADER_X_RATELIMIT_LIMIT));
        System.out.println("remaining: " + test.get(Constants.HEADER_X_RATELIMIT_REMAINING));
        System.out.println("reset: " + test.get(Constants.HEADER_X_RATELIMIT_RESET));
        System.out.println("current: " + (System.currentTimeMillis() / 1000));

        // for each repo, if first level and second level dir don't contain pom.xml or build.gradle; skip it
        System.out.println("sample output");
    }
}
