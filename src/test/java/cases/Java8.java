package cases;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhengfan on 2016/12/26 0026.
 */
public class Java8 {

    @Test
    public void lambdaTest() {
        Arrays.asList("a", "b", "d").forEach(e -> System.out.println(e));
        Arrays.asList( "a", "b", "d" ).forEach( ( String e ) -> System.out.println( e ) );
        Arrays.asList( "a", "b", "d" ).forEach( e -> {
            System.out.print( e );
        } );

        Arrays.asList( "a", "b", "d" ).sort( ( e1, e2 ) -> e1.compareTo( e2 ) );

        Arrays.asList( "a", "b", "d" ).sort( ( e1, e2 ) -> {
            int result = e1.compareTo( e2 );
            return result;
        } );
    }

    public void pattern(){
//        Pattern p = Pattern.compile("name: '([^']*)'");
//        lines.map(p::matcher)
//                .filter(Matcher::matches)
//                .findFirst()
//                .ifPresent(matcher -> System.out.println(matcher.group(1)));
    }
}
