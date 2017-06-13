package cases;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharSet;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import sun.nio.cs.UnicodeEncoder;
import use.demo.utils.JSONUtils;

/**
 * Created by zhengfan on 2017/4/17 0017.
 */
public class Tenaa {

    private Connection initConnection(String url) {
        return Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .timeout(10000)
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

    @Test
    public void checkLc() throws IOException {
        File f = new File("append.json");
        f.delete();
        List<String> lcs = FileUtils.readLines(new File("append.lc"), "utf-8");
        List<String> lcsALL = FileUtils.readLines(new File("lc.bak"), "utf-8");
        Set<String> set = new HashSet<>(lcsALL);
        List<String> list = new ArrayList<>(lcs.size());
        int count = 0;
        for (String lc : lcs) {
            count++;
            if (set.contains(lc)){
                continue;
            }
            try {
                Document rep = initConnection("http://sh01-sjws-cache43.sh01.baidu.com:8123/api/getApp?lc=" + lc).get();
                if (rep.text().contains("bd_code")){
                    list.add(String.format("2017-06-13 12:00:53    %s",rep.text()));
                }
            }catch (Exception e){
                System.err.println(lc);
            }

            System.out.println(lcs.size() - count);
            if (count == 1000){
                FileUtils.writeLines(f,"UTF-8",list,true);
                list.clear();
            }
        }
        FileUtils.writeLines(f,"UTF-8",list,true);

    }

    @Test
    public void DDD() throws IOException {
        File f = new File("append.json");
        List<String> lcsALL = FileUtils.readLines(f, "utf-8");
        List<String> list = new ArrayList<>(lcsALL.size());
        for (String ss : lcsALL) {
            list.add(decode2(ss));
        }
        f.delete();
        FileUtils.writeLines(f,"UTF-8",list,true);

    }
    public static String decode2(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\\' && chars[i + 1] == 'u') {
                char cc = 0;
                for (int j = 0; j < 4; j++) {
                    char ch = Character.toLowerCase(chars[i + 2 + j]);
                    if ('0' <= ch && ch <= '9' || 'a' <= ch && ch <= 'f') {
                        cc |= (Character.digit(ch, 16) << (3 - j) * 4);
                    } else {
                        cc = 0;
                        break;
                    }
                }
                if (cc > 0) {
                    i += 5;
                    sb.append(cc);
                    continue;
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

}
