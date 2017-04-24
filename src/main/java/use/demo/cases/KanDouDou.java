package use.demo.cases;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import use.demo.BaseDownLoad;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhengfan on 2016/11/29 0029.
 */
public class KanDouDou extends BaseDownLoad {

    private static Logger LOGGER = LoggerFactory.getLogger(KanDouDou.class);

    private String baseDir = "c:\\KanDouDou\\";

    private int pages = 100;

    private int index = 1;

    private int sleep = 0;

    private File historyFile = new File("download/" + "download.list");

    private Set<String> historySet = new HashSet<String>();

    private Set<String> blackIP = new HashSet<String>();
    private List<String> arrayList = new ArrayList<String>();

    private static final String INDEX_URL = "http://d.kankandou.com/";

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public void initDir() {
        try {
            if (SystemUtils.IS_OS_LINUX) {
                FileUtils.forceMkdir(new File("download"));
                baseDir = "download/";
                if (historyFile.exists()) {
                    List<String> ss = FileUtils.readLines(historyFile, "utf-8");
                    System.out.println("loading history file ...");
                    for (String url : ss) {
                        historySet.add(url.trim().replace("\r", "").replace("\n", ""));
                    }

                    FileUtils.forceDelete(historyFile);
                    for (String url : historySet) {
                        FileUtils.writeStringToFile(historyFile, url + "\n", "utf-8", true);
                    }

                    //historySet.addAll(ss);
                }
            } else {
                FileUtils.forceMkdir(new File(baseDir));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void sleep() {
        if (sleep > 0) {
            try {
                TimeUnit.SECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void downLoadBook(String url) throws IOException {
        sleep();
        if (hasDownLoad(url)) {
            return;
        }
        LOGGER.info("parse : " + url);
        String ip = setProxy();
        try {
            File failFile = new File(baseDir + "download.fail.list");
            Connection.Response rsp = initConnection(url).execute();
            if (!(rsp.header("Content-Disposition") + "").contains("attachment")) {
                FileUtils.writeStringToFile(failFile, url + "\n", "utf-8", true);
                LOGGER.info("不是可以下载的链接... ： " + url);
            }
            String disposition = new String(rsp.header("Content-Disposition").getBytes("ISO-8859-1"), "UTF8");
            String fileName = regex("filename=\"(.*)\"", disposition, 1);
            if (fileName == null) {
                LOGGER.info("获取文件名失败... ： " + rsp.header("Content-Disposition"));
                FileUtils.writeStringToFile(failFile, url + "\n", "utf-8", true);
                return;
            }
            File tmpFile = new File(baseDir + fileName);
            if (tmpFile.exists()) {
                LOGGER.info("文件已下载  :{}", fileName);
                FileUtils.writeStringToFile(failFile, url + "\n", "utf-8", true);
                return;
            }

            LOGGER.info("正在保存文件 " + fileName);
            FileUtils.writeByteArrayToFile(new File(baseDir + fileName), rsp.bodyAsBytes());
            FileUtils.writeStringToFile(historyFile, url + "\n", "utf-8", true);
        } catch (SocketTimeoutException e) {
            ;
            blackIP.add(ip);
        } catch (ConnectException e) {
            ;
            blackIP.add(ip);
        } catch (SocketException e) {
            ;
            blackIP.add(ip);
        }

    }

    public void start() throws IOException {
        for (int i = index; i <= pages; i++) {
            try {
                downLoadBook(INDEX_URL + "simple/down/" + i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasDownLoad(String url) throws IOException {
        if (historySet.contains(url)) {
            LOGGER.info("hit :{}", url);
            return true;
        }

        // 方式一
        try {
            HttpURLConnection.setFollowRedirects(false);
            // note : you may also need
            //                    HttpURLConnection.setInstanceFollowRedirects(false);
            String ip = setProxy();
            //            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip.split(":")[0], Integer
            // .valueOf(ip.split
            //                    (":")[1])));
            //            HttpURLConnection con =
            //                    (HttpURLConnection) new URL(url).openConnection(proxy);
            HttpURLConnection con =
                    (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("HEAD");
            con.setConnectTimeout(1000); //set timeout to 2 seconds
            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
            String disposition = con.getHeaderField("Content-Disposition");

            if (disposition == null || disposition.isEmpty()) {
                LOGGER.info("download failed :{}", url);
                FileUtils.writeStringToFile(historyFile, url + "\n", "utf-8", true);
                return true;
            }

            disposition = new String(disposition.getBytes("ISO-8859-1"), "UTF-8");
            String fileName = regex("filename=\"(.*)\"", disposition, 1);
            File tmpFile = new File(baseDir + fileName);
            if (tmpFile.exists()) {
                LOGGER.info("文件已下载 :{}", fileName);
                FileUtils.writeStringToFile(historyFile, url + "\n", "utf-8", true);
                return true;
            }

            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private String setProxy() {
        if (arrayList.size() > 0) {
            int offset = new Random().nextInt(arrayList.size());
            String ip = arrayList.get(offset);
            if (!blackIP.contains(ip)) {
                System.setProperty("http.proxyHost", ip.split(":")[0]);
                System.setProperty("http.proxyPort", ip.split(":")[1]);
                //routePlanner.setProxy(new HttpHost(ip.split(":")[0], Integer.valueOf(ip.split(":")[1])));
                System.out.println(ip);
                return ip;
            }

        }
        System.setProperty("http.proxyHost", "");
        System.setProperty("http.proxyPort", "");
        return "";
    }

    public void initProxy() throws IOException {
        for (int i = 0; i < 10; i++) {
            Document doc = initConnection("http://www.kuaidaili.com/proxylist/" + i).get();
            for (Element element : doc.select("tbody tr")) {
                String ip = element.select("td:eq(0)").text();
                if (null != regex("\\d+.\\d+.\\d+.\\d+", ip, 0)) {
                    String port = element.select("td:eq(1)").text();
                    LOGGER.info("scan ip : {}:{}", ip, port);
                    arrayList.add(ip + ":" + port);
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {

        if (args.length > 1 && "help".equals(args[0].toLowerCase()) || "-help".equals(args[0].toLowerCase())) {
            System.out.println("USE: java -jar xxx.jar start end sleep useProxy");
            System.out.println("USE: java -jar xxx.jar 1 100 0 y");
            System.exit(0);
        }

        KanDouDou kanDouDou = new KanDouDou();
        if (args.length >= 1) {
            kanDouDou.setIndex(Integer.valueOf(args[0]));
        }

        if (args.length >= 2) {
            kanDouDou.setPages(Integer.valueOf(args[1]));
        }

        if (args.length >= 3) {
            kanDouDou.setSleep(Integer.valueOf(args[2]));
        }

        if (args.length >= 4 && args[3].equalsIgnoreCase("y")) {
            kanDouDou.initProxy();
        }

        kanDouDou.initDir();
        kanDouDou.start();
    }
}
