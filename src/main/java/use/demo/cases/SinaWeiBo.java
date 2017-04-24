package use.demo.cases;

import org.codehaus.jackson.JsonNode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import use.demo.utils.JSONUtils;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by micky on 2016/10/26.
 */
public class SinaWeiBo {

    private String cookie = "";

    private static final Pattern HTML_CONTENT_PATTERN =
            Pattern.compile("<script>FM.view\\(\\{.*?Pl_Official_MyProfileFeed__24.*?html\":\"(.*)\"\\}\\)</script>");

    private static final Pattern HTML_REFRESH_PATTERN =
            Pattern.compile("<meta http-equiv=\"refresh\" content=\"0; url=.*?;(http.*?);");

    private Connection initConnection(String url) {
        return Jsoup.connect(url).header("Host", "weibo.com")
                .header("Referer", "http://weibo.com/u/3314446737/home?topnav=1&wvr=6")
                .header("Cookie", cookie).ignoreHttpErrors(true).timeout(6000).followRedirects(true)
                .ignoreContentType(true);

    }

    public void homePage() throws IOException {
        final String homePage = "http://weibo.com/234399467?profile_ftype=1&is_all=1&sudaref=weibo.com&retcode=6102#39";
        Connection.Response rsp = initConnection(homePage).execute();
        parse(rsp.body());
    }

    public Connection.Response parsePageRefresh(Connection.Response rsp) {
        Matcher m = HTML_REFRESH_PATTERN.matcher(rsp.body());
        if (m.find()) {
            String url = m.group(1);
            try {
                return initConnection(url).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rsp;
    }

    public void small() throws IOException {
        final String homePage =
                "http://weibo.com/aj/v6/comment/small?ajwvr=6&act=list&mid=4035585410175985&uid=3314446737&isMain"
                        + "=true&dissDataFromFeed=%5Bobject%20Object%5D&ouid=2253566775&location=page_100505_home"
                        + "&comment_type=0&_t=0&__rnd=1477643319245";
        Connection.Response rsp = parsePageRefresh(initConnection(homePage).execute());
        JsonNode root = JSONUtils.toObject(rsp.body());
        if (!root.isMissingNode()) {
            System.out.println(root.path("data").path("html").asText());
        }
    }

    private void parse(String body) {
        Matcher m = HTML_CONTENT_PATTERN.matcher(body);
        if (m.find()) {
            String html = m.group(1).replaceAll("\\\\(\")", "$1").replaceAll("\\\\(/)", "$1");
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("div.WB_text.W_f14");
            int counter = 1;
            for (Element e : elements) {
                counter++;

                //微博置顶 pass 不处理;
                if (1 == counter && e.select("> a[title=\"微博会员特权\"]").size() > 0) {
                    continue;
                }

                //当前消息中存在 网页链接地址
                Elements hrefElements = e.select("> a[title=\"网页链接\"]");
                if (hrefElements.size() > 0) {
                    for (Element hrefElement : hrefElements) {
                        System.out.println(hrefElement.attr("href"));
                    }
                    continue;
                }

                //页面不存在地址,需要抓取评论
                //http://weibo.com/aj/v6/comment/small?ajwvr=6&act=list&mid=4035585410175985&uid=3314446737&isMain
                // =true&dissDataFromFeed=%5Bobject%20Object%5D&ouid=2253566775&location=page_100505_home
                // &comment_type=0&_t=0&__rnd=1477643319245

            }
        }
    }
}
