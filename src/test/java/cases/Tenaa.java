package cases;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharSet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import sun.nio.cs.UnicodeEncoder;

/**
 * Created by zhengfan on 2017/4/17 0017.
 */
public class Tenaa {

    private Connection initConnection(String url) {
        return Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .timeout(3000)
                .followRedirects(false)
                .ignoreContentType(true).validateTLSCertificates(false);

    }

    @Test
    public void start() throws IOException {
        File f = new File("t.txt");
        f.delete();
        for (int i = 0;i<=51;i++){
            crawl("http://shouji.tenaa.com.cn/JavaScript/MobileGoodsStation.aspx?DM=" + i
                    + "|SSSJ|;;,002003,002004;;;;;"
                    + ";,"
                    + "`android`%20;&type=03");
        }

    }



    @Test
    public void model() throws IOException {
        //
        String args[] = {"三星", "酷派", "联想", "LG", "TCL", "锤子", "朵唯", "海信", "华为", "乐视", "欧珀","魅族",
                "荣耀", "索尼", "维沃", "一加", "中兴", "小米", "宏达", "金立"};
        File f = new File("t.txt");
        f.delete();
        for (String ss : args){
            crawl("http://shouji.tenaa.com.cn/JavaScript/MobileGoodsStation.aspx?DM="+java.net.URLEncoder
                    .encode(";;;;;;;,`" + ss + "`;,`android` ;|", "utf-8") + "SSSJ&type=01");
        }
    }


    public void crawl(String index) throws IOException {
        System.out.println(index);
        Document req = initConnection(index).get();
        Pattern p = Pattern.compile("comparaAdd\\('(.*)'\\)");
        Matcher m = p.matcher(req.html());
        List<String> list = new ArrayList<>();
        File f = new File("t.txt");
        while (m.find()){
            String[] arr = m.group(1).split("_");
            String name= arr[0];
            String model = arr[1];
            String ss = String.format("%s\t%s",name,model);
            System.out.println(ss);
            list.add(ss);
        }
        FileUtils.writeLines(f, list, true);

    }

}
