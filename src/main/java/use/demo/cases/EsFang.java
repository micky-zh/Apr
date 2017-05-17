package use.demo.cases;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import use.demo.BaseDownLoad;

/**
 * Created by zhengfan on 2017/5/8 0008.
 */

public class EsFang extends BaseDownLoad {

    private static Logger LOGGER = LoggerFactory.getLogger(EsFang.class);

    private String index = "http://esf.fang.com/map/";

    private void init() throws IOException {
        Document doc = initConnection(index).get();
        for (Element element : doc.select("#area")) {

        }
    }
}
