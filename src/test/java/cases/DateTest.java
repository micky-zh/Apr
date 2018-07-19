package cases;

import java.text.SimpleDateFormat;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

/**
 * Created by zhengfan on 2016/12/27 0027.
 */
public class DateTest {

    public void print() {
        new SimpleDateFormat("");
    }

    @Test
    public void testCompare() {
        assert compare("20170102", "20170102");
        assert !compare("20170102", "20170103");
        assert compare("20170102", "20170101");
        assert compare("asxzz", "20170101");
    }

    private boolean compare(String version, String after) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        LocalDate date = formatter.parseLocalDate(version);
        LocalDate end = formatter.parseLocalDate(after);

        return date.isEqual(end) || date.isAfter(end);

    }
}
