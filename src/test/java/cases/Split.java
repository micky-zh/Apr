package cases;

import org.junit.Test;

/**
 * Created by zhengfan on 2016/12/7 0007.
 */
public class Split {

    @Test
    public void split(){
        String str =  "a|b,c";
        for (String s: str.split("[|,]")){
            System.out.println(s);
        }
    }

    @Test
    public void replace(){
        String str =  "a|b,c";
        System.out.println(str.replaceAll("b|c","-"));
        System.out.println(str.replaceAll("[bc]","-"));
    }
}
