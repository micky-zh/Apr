package use.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhengfan on 2016/11/29 0029.
 */
public abstract class BaseDownLoad {

    final int timeout = 30000;

    protected final Map<String, String> header = new HashMap<>();

    protected Connection initConnection(String url) {
        return Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .timeout(timeout)
                .followRedirects(false)
                .ignoreContentType(true).validateTLSCertificates(false);

    }

    {
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        header.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        header.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        header.put("Cache-Contro", "max-age=0");
        header.put("Accept-Encoding", "gzip");

    }

    protected Connection initConnectionWithHeader(String url){
        Connection c = initConnection(url);
        for (Map.Entry<String, String> entry : header.entrySet()) {
            c.header(entry.getKey(),entry.getValue());
        }
        return c;
    }

    protected String regex(String regex, String body, int group) {
        Matcher m = Pattern.compile(regex).matcher(body);
        if (m.find()) {
            return m.group(group);
        }
        return null;
    }


}
