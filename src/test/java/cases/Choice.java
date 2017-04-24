package cases;

import org.junit.Test;

import java.util.Random;

/**
 * Created by zf on 2016/12/31.
 */
public class Choice {

    @Test
    public void makeChoice() {
        String[] arr = {"铁道飞虎队", "长城"};
        for (int i = 1; i <= 3; i++)
            System.out.println(arr[new Random().nextInt(2)]);

    }

    double 年度扣税额 = 0;
    double 汇率 = 6.8845D;
    double 股价 = 178.08D;

    @Test
    public void findMax() {

        double 股数 = 5D;

        double 纳税收入 = 股价 * 股数;
        System.out.println(纳税收入);

        double 应该交税额 = tax(纳税收入);
        System.out.println(应该交税额);

        double 应收费用 = 30.05D;
        System.out.println(纳税收入 - 应收费用 - 应该交税额);


    }

    public double tax(double 纳税收入) {
        double taxRate = 0;
        double Qdf = 0;
        if (纳税收入 > .01D && 纳税收入 <= 1500D) {
            taxRate = .03D;
            Qdf = 0;
        }

        if (纳税收入 > 1500.01D && 纳税收入 <= 4500D) {
            taxRate = .1D;
            Qdf = 105;
        }


        if (纳税收入 > 4500.01D && 纳税收入 <= 9500D) {
            taxRate = .2D;
            Qdf = 555;
        }

        return (纳税收入 / 12 * taxRate - Qdf) * 12 - 年度扣税额;

    }

}
