package use.demo.cases;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import use.demo.BaseDownLoad;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhengfan on 2016/11/25 0025.
 * for download '妹子图'
 */
public class JianDan extends BaseDownLoad {

    private static Logger LOGGER = LoggerFactory.getLogger(JianDan.class);

    private String baseDir = "c:\\SinaImage\\";

    private int pages = 10;

    private int index = 1;

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public void setIndex(int index) {
        this.index = index;
    }


    private String INDEX_URL = "https://jandan.net/ooxx";

    private void init() {
        try {
            FileUtils.forceMkdir(new File(baseDir));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


//    @Test
//    public void getPath() throws MalformedURLException {
//        URL url = new URL("http://ww1.sinaimg.cn/mw690/707c5757gw1f9shjpofvrg20b4068b29.gif");
//
//        System.out.println(FilenameUtils.getBaseName(url.getPath())); // -> file
//        System.out.println(FilenameUtils.getExtension(url.getPath())); // -> xml
//        System.out.println(FilenameUtils.getName(url.getPath())); // -> file.xml
//    }

    public void downLoadImage(String url) throws IOException {
        final String fileName = FilenameUtils.getName(new URL(url).getPath());
        File tmpFile = new File(baseDir + fileName);

        if (tmpFile.exists()) {
            LOGGER.info("pass file already exit! file :{}", fileName);
            return;
        }
        LOGGER.info("downLoad url: {}", url);
        for (int i = 1; i <= 3; i++) {
            try {
                Connection.Response rep = initConnection(url).execute();
                FileUtils.writeByteArrayToFile(new File(baseDir + fileName), rep.bodyAsBytes());
                break; // Break immediately if successful
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("retry failed to download url : " + url);
            }
        }

    }

    private void parseCurrent() throws IOException {
        Connection.Response r = initConnectionWithHeader(INDEX_URL).execute();
        Document doc = r.parse();
        Element element = doc.select("span.current-comment-page").first();
        if (element == null) {
            LOGGER.warn("not found page number info.");
            return;
        }

        Matcher m = Pattern.compile("\\d+").matcher(element.text());
        int last = Integer.MAX_VALUE;
        if (m.find()) {
            index = Integer.valueOf(m.group());
            last = index - pages;
            LOGGER.info("current page is {}", index);
        }

        final String pageUrl = INDEX_URL + "/page-";
        for (int i = index; i > last; i--) {
            try {
                parseContent(pageUrl + i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void parseContent(String url) throws IOException {
        LOGGER.debug("current request url is {}", url);
        Document doc = initConnection(url).get();
        for (Element element : doc.select("a.view_img_link")) {
            //System.out.println(element.attr("href"));
            String href = element.attr("href");
            downLoadImage(href.startsWith("//") ? "http:" + href : href);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1 && ("help".equals(args[0].toLowerCase()) || "-help".equals(args[0].toLowerCase()))) {
            System.out.println("USE: java -jar xxx.jar  dir start end");
            System.out.println("USE: java -jar xxx.jar 1 100 0 y");
            System.exit(0);
        }

        JianDan jiandan = new JianDan();
        if (args.length >= 1) {
            jiandan.setBaseDir(args[0]);
        }

        if (args.length >= 2) {
            jiandan.setIndex(Integer.valueOf(args[1]));
        }

        if (args.length >= 3) {
            jiandan.setPages(Integer.valueOf(args[2]));
        }
        jiandan.init();
        jiandan.parseCurrent();
    }
}
