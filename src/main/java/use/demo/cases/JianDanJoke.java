package use.demo.cases;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import use.demo.BaseDownLoad;

/**
 * Created by zhengfan on 2017/5/19 0019.
 */
public class JianDanJoke extends BaseDownLoad {
    private static Logger LOGGER = LoggerFactory.getLogger(JianDanJoke.class);
    private String INDEX_URL = "http://jandan.net/duan";
    int current = -1;

    public int parseCurrentPage() throws IOException {
        Connection.Response r = initConnectionWithHeader(INDEX_URL).execute();
        Document doc = r.parse();
        Element element = doc.select("span.current-comment-page").first();
        if (element == null) {
            LOGGER.warn("not found page number info.");
            return 0;
        }

        Matcher m = Pattern.compile("\\d+").matcher(element.text());
        if (m.find()) {
            current = Integer.valueOf(m.group());
            LOGGER.info("current page is {}", current);
        }
        return current;
    }

    public void parse() {
        for (int i = current; i >= current - 1000; i--) {
            try {
                parseContent("http://jandan.net/duan/page-" + i + "#comments");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseContent(String url) throws IOException {
        LOGGER.debug("current request url is {}", url);
        Document doc = initConnection(url).get();
        for (Element element : doc.select("div.text")) {
            //System.out.println(element.attr("href"));
            if (element.text().contains("ppt") || element.text().contains("PPT") || element.text().contains("婚礼"))
                System.out.println(element.text());

        }
    }

    public static void main(String[] args) throws IOException {
        JianDanJoke jianDanJoke = new JianDanJoke();
        jianDanJoke.parseCurrentPage();
        jianDanJoke.parse();
    }
}
