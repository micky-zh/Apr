package use.demo.cases;

import org.jsoup.Connection;
import use.demo.BaseDownLoad;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhengfan on 2016/12/30 0030.
 */
public class Family extends BaseDownLoad {

    private String index = "http://chanpin.family.baidu.com/article/39561";

    {
        header.put("Referer", index);
        header.put("Host", "chanpin.family.baidu.com");
        header.put("Origin", "http://chanpin.family.baidu.com");
        header.put("Cookie", "Hm_lvt_97a3ccc58f72810a014745c167a12ffc=1467011166,1468220355,1468819923; BDUSS=VJFQWlKYkd3QWZpdTFmemtuNmhnSDFoSzNIYzFXUS14aUJoRElIdEYtS3NCbDVZSVFBQUFBJCQAAAAAAAAAAAEAAACkvr4N1u2wy73kwM~X5tfaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKx5NliseTZYW; MCITY=-131%3A; BAIDUID=C546232359156D3ECF80FFCB1E32E7E5:FG=1; BIDUPSID=C546232359156D3ECF80FFCB1E32E7E5; PSTM=1482114569; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; PSINO=2; H_PS_PSSID=1446_21116_20697_21554_20929; express.sid=s%3AyRXGTQymWtGjT5HNHRgu0_neWgmgBS55.byhzmah7%2Brjc6jvHdNqJbcuqJnnJ1n4ou%2FxNtbcU%2Bc4; Hm_lvt_e5c8f30b30415b1fc94d820ba9d4d08c=1481685550,1481693137,1481790995,1482719069; Hm_lpvt_e5c8f30b30415b1fc94d820ba9d4d08c=1483082731");
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    }

    private String comment = "http://chanpin.family.baidu.com/asyncComment";

    private int sleep = 1000;

    private int step = 1;

    public void setStep(int step) {
        this.step = step;
    }

    private int[] winFloor = {1600};


    public void setWinFloor(String winFloor) {
        if (!winFloor.contains(",")) {
            this.winFloor = new int[]{Integer.valueOf(winFloor)};
            return;
        }

        String strArr[] = winFloor.split(",");
        int arr[] = new int[strArr.length];
        int c = 0;
        for (String str : winFloor.split(",")) {
            arr[c++] = Integer.valueOf(str.trim());
        }
        this.winFloor = arr;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    private Pattern pattern = Pattern.compile("全部评论 <em>(\\d+)</em>条");

    private void sleep() {
        try {
            if (sleep > 0)
                TimeUnit.MILLISECONDS.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private int parse() throws IOException {
        Connection.Response rsp = initConnectionWithHeader(comment).data("articleId", "39561").method(Connection.Method.POST).execute();
        if (200 == rsp.statusCode()) {
            Matcher m = pattern.matcher(rsp.body());
            if (m.find()) {
                System.out.println(m.group(1));
                return Integer.valueOf(m.group(1));
            }
            //rsp.parse().select("comment-total-count");
        }
        return -1;
    }


    private boolean isMatch(int num) {
        if (-1 == num)
            return false;

        for (int i : winFloor) {
            if (i - num == step) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        for (; ; ) {
            try {
                if (isMatch(parse())) {
                    //postComment todo
                    break;
                }
                sleep();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        if (args.length > 1 && ("help".equals(args[0].toLowerCase()) || "-help".equals(args[0].toLowerCase()))) {
            System.out.println("USE: java -jar xxx.jar sleep step");
            System.out.println("USE: java -jar xxx.jar 1000 1");
            System.exit(0);
        }

        Family family = new Family();
        if (args.length >= 1) {
            family.setSleep(Integer.valueOf(args[0]));
        }

        if (args.length >= 2) {
            family.setStep(Integer.valueOf(args[1]));
        }

        if (args.length >= 3) {
            family.setWinFloor(args[2]);
        }

        family.start();
    }
    //this.s

}
