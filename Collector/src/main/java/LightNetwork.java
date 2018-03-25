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
    static final String HEADER_CONTENT = "Content";
    static final String USER_AGENT = "UofA SEI 2018 Project 1";

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

    public static HashMap<String, String> lightHttpRequest(final String url) {
        HashMap<String, String> ret = new HashMap<>();

        InputStream inputStream;
        try {
            URL localURL = new URL(url);
			HttpURLConnection httpURLConnection = (HttpURLConnection)localURL.openConnection();
            httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                inputStream = httpURLConnection.getErrorStream();
            } else if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                System.out.println("403 forbidden: " + System.currentTimeMillis());
                System.exit(-1);
                return ret;
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
                ret.put(entry.getKey(), "" + entry.getValue());
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
}
