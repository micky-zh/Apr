package use.demo.cases;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import use.demo.BaseDownLoad;

/**
 * Created by zhengfan on 2016/11/25 0025.
 * for download '妹子图'
 */
public class JianDan extends BaseDownLoad {

    private static Logger LOGGER = LoggerFactory.getLogger(JianDan.class);

    private String INDEX_URL = "https://jandan.net/ooxx";

    private String baseDir = "c:\\SinaImage\\";

    private int pages = 10;

    private int index = 1;

    private OkHttpClient client;

    private ScriptEngineManager factory = new ScriptEngineManager();

    private ScriptEngine engine = factory.getEngineByName("JavaScript");

    public JianDan() throws Exception {
        // read script file
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        engine.eval(Files.newBufferedReader(Paths.get(s + "/scripts/jiandan.js"), StandardCharsets.UTF_8));

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    private void init() {
        try {
            FileUtils.forceMkdir(new File(baseDir));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void downLoadImage(String url) throws IOException {
        final String fileName = FilenameUtils.getName(new URL(url).getPath());
        File tmpFile = new File(baseDir + fileName);

        if (tmpFile.exists()) {
            LOGGER.info("pass file already exit! file :{}", fileName);
            return;
        }
        LOGGER.info("downLoad url: {}", url);
        for (int i = 1; i <= 3; i++) {
            try {
                // Connection.Response rep = initConnection(url).execute();
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();
                ResponseBody rep = response.body();
                FileUtils.writeByteArrayToFile(new File(baseDir + fileName), rep.bytes());
                break; // Break immediately if successful
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.out.println("retry failed to download url : " + url);
            }
        }

    }

    private String runJS(String code) throws ScriptException, IOException, NoSuchMethodException {

        Invocable inv = (Invocable) engine;
        // call function from script file
        Object ss =
                inv.invokeFunction("jdymYkosnJZRKejkAMubgc9PgyjpU8tD31", code, "tcrbe7l5qVTQjh5Hr9F2oC9TUPutBpb5");
        return ("http:" + ss).replaceAll("cn/.*?/", "cn/large/").replaceAll("//ww.","//wx4");
    }

    private void parse(String url, int counter) throws IOException, ScriptException, NoSuchMethodException {
        LOGGER.info("current request url is {}, page : {}", url, counter);
        Document doc = initConnection(url).get();
        for (Element element : doc.select("span.img-hash")) {
            String image = runJS(element.text());
            downLoadImage(image);
        }
        if (counter <= this.pages) {
            Element e = doc.select(".previous-comment-page").first();
            String nextPage = "http:" + e.attr("href");
            parse(nextPage, counter + 1);
        }

    }

    private void start() throws Exception {
        parse(INDEX_URL, 0);
    }

    public static void main(String[] args) throws Exception {
        JianDan j = new JianDan();
        j.init();
        j.pages = 1000;
        j.start();
    }
}
