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
        for (int i = 1; i <= 3; i++) {
            System.out.println(arr[new Random().nextInt(2)]);
        }

    }

    double 年度扣税额 = 0;
    double 汇率 = 6.8845D;
    double 股价 = 178.08D;
    double 应收费用 = 30.05D;

    @Test
    public void Total() {
        for (int i = 1; i <= 25; i++) {
            Double 纳税收入 = 股价 * i;
            Double 应该交税额 = tax(纳税收入 * 汇率);
            Double 剩余股数 = (纳税收入 - 应该交税额 - 应收费用) / 股价;
            Double 总缴税 = 应该交税额 * (25 / i);

            int rem = 25 % i;
            Double 总操作费 = (25 / i) * 30.05;
            if (rem != 0) {
                总缴税 = 总缴税 + tax(股价 * rem * 汇率);
                总操作费 = 总操作费  + 30.05;
            }
            Double 总收益 = (25 * 股价) - 总缴税.intValue() - 总操作费.intValue();
            System.out.println(String.format("per = %d, 纳税收入 = %.2f, 应该交税额 = %.2f $, 总剩余股数 = %s, 总缴税 = %.2f $ "
                            + ",总操作费 = %.2f $ ,总收益 = %.2f $"
                    , i, 纳税收入, 应该交税额, 剩余股数.intValue() * (25 / i), 总缴税, 总操作费, 总收益));
        }

    }

    public double tax(double 纳税收入) {
        double taxRate = 0;
        double Qdf = 0;

        if ( 纳税收入 <= 1500D) {
            taxRate = .03D;Qdf = 0;
        }

        else if ( 纳税收入 <= 4500D) {
            taxRate = .1D;Qdf = 105;
        }

        else if ( 纳税收入 <= 9000D) {
            taxRate = .2D;Qdf = 555;
        }

        else if ( 纳税收入 <= 35000D) {
            taxRate = .25D;Qdf = 1005;
        }

        else if ( 纳税收入 <= 55000D) {
            taxRate = .3D;Qdf = 2755;
        }

        else if (纳税收入 <= 80000D) {
            taxRate = .35D;Qdf = 5505;
        }
        else {
            taxRate = .45D;Qdf = 13505;
        }
        return (纳税收入 * taxRate - Qdf)  / 汇率;


    }

}
