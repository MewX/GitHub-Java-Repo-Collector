package au.edu.uofa.sei.assignment1.collector;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Light Network
 * *
 * This class achieve the basic network protocol:
 * HttpPost ...
 **/

public class LightNetwork {
    public static final String HEADER_CONTENT = "Content";
    public static final String USER_AGENT = "UofA SEI 2018 Project 1";

    /**
     * Encode UTF-8 character to http postable style. For example: "妹" = "%E5%A6%B9"
     *
     * @param str input string
     * @return result encoded string or empty string
     */
    public static String encodeToHttp(String str) {
        String enc;
        try {
            enc = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            enc = ""; // prevent crash
        }
        return enc;
    }

    public static Map<String, String> lightHttpRequest(final Map<String, String> previousRequestResult, final String url) {
        waitUntilRefresh(previousRequestResult);
        return lightHttpRequest(url);
    }

    public static Map<String, String> lightHttpRequest(final String url) {
        System.err.println(" requesting: " + url);

        HashMap<String, String> ret = new HashMap<>();
        InputStream inputStream;
        try {
            URL localURL = new URL(url);
			HttpURLConnection httpURLConnection = (HttpURLConnection)localURL.openConnection();
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            httpURLConnection.setConnectTimeout(16000);
            httpURLConnection.setReadTimeout(16000);

            String basicAuth = "Basic " + new String(new Base64().encode("mseopt:mseopt@gmail.com".getBytes()));
            httpURLConnection.setRequestProperty ("Authorization", basicAuth);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                inputStream = httpURLConnection.getErrorStream();
            } else if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                // not exiting directly, but keeping the response
                System.out.println("403 forbidden: " + System.currentTimeMillis());
                inputStream = httpURLConnection.getErrorStream();
            } else if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("ERROR: got " + httpURLConnection.getResponseCode() + " in " + url);
                inputStream = httpURLConnection.getErrorStream();
            } else {
                inputStream = httpURLConnection.getInputStream();
            }

            // save contents
            byte[] b = new byte[1024];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (httpURLConnection.getContentEncoding() != null && httpURLConnection.getContentEncoding().toLowerCase().contains("gzip")) {
                // using 'gzip'
                inputStream = new GZIPInputStream(new BufferedInputStream(inputStream));
            }
            int len;
            while ((len = inputStream.read(b)) != -1) byteArrayOutputStream.write(b, 0, len);
            byteArrayOutputStream.close();

            inputStream.close();
            byteArrayOutputStream.close();

            // save to the map
            for (Map.Entry<String, List<String>> entry : httpURLConnection.getHeaderFields().entrySet()) {
                String headerName = entry.getKey();
                StringBuilder sb = new StringBuilder();
                for (String value : entry.getValue()) {
                    sb.append(value).append(" ");
                }
                ret.put(headerName, sb.toString().trim());
            }

            ret.put(HEADER_CONTENT, new String(byteArrayOutputStream.toByteArray(), "UTF-8"));
        } catch (Exception e) {
            // e.printStackTrace();
            ret.put(HEADER_CONTENT, "");
        }
        return ret;
    }

    /**
     * Give direct url to download file in one time, so this only fits small size files.
     *
     * @param url direct file url with extension
     * @return return correct bytes or null
     */
    public static String lightHttpDownload(final String url) {
        return lightHttpRequest(url).get(HEADER_CONTENT);
    }

    public static void waitUntilRefresh(Map<String, String> requestResult) {
        if (requestResult == null) return;

        long remaining  = Long.valueOf(requestResult.get(Constants.HEADER_X_RATELIMIT_REMAINING));
        long total = Long.valueOf(requestResult.get(Constants.HEADER_X_RATELIMIT_LIMIT));
        long resetTime = Long.valueOf(requestResult.get(Constants.HEADER_X_RATELIMIT_RESET));
        long time = System.currentTimeMillis() / 1000;
        System.err.format("Rem: %d of %d, time: %d resetting at %d\n", remaining, total, time, Integer.valueOf(requestResult.getOrDefault(Constants.HEADER_X_RATELIMIT_RESET, "0")));
        if (remaining <= 1 && resetTime - time > 0) {
            System.err.format("  waiting for cooling down ... (used: %d; cooling down: %d/%d\n", total, time, resetTime);
            try {
                Thread.sleep((resetTime - time + 1) * 1000); // wait
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
