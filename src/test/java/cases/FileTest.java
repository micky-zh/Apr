package cases;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Created by zhengfan on 2017/4/27 0027.
 */
public class FileTest {

    @Test
    public void makeKafkaFile() throws IOException {

        File f = new File("lcs.txt");
        File fn = new File("sync.txt");
        fn.delete();
        List<String> list = FileUtils.readLines(f, "utf-8");
        List<String> list1 = new ArrayList<>(list.size());
        for (String ss:list){
            list1.add(String.format("2017-04-27 12:07:59    %s", ss));
        }
        FileUtils.writeLines(fn, list1, false);
    }
}
