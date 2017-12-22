package cases;

import org.junit.Test;

/**
 * Created by zhengfan on 2017/10/17 0017.
 */
public class SwitchTest {


    @Test
    public void choose(){
        String type = "a";
        switch (type){
            case "a":
            case "b":
                type="B";
            case "c":
            default:
                System.out.println(type);
        }
    }
}
