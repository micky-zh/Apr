package cases;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

/**
 * Created by zhengfan on 2017/11/3 0003.
 */
public class DateTimeTest {

    @Test
    public void testRange() throws ParseException {
        org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate startDate = LocalDate.parse("2017-11-01 00:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDate endDate = LocalDate.parse("2017-11-02 11:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        //
        //        int hours = Hours.hoursBetween(startDate, endDate).getHours();
        //        System.out.println(hours);

        List<LocalDate> daysRange =
                Stream.iterate(startDate, date -> date.plusDays(1))
                        .limit(Days.daysBetween(startDate, endDate).getDays())
                        .collect(Collectors.toList());
        daysRange.add(endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Collections.sort(daysRange, (s1, s2) -> s1.compareTo(s2));

        for (LocalDate date : daysRange) {
            System.out.println(date);
        }
    }

    @Test
    public void testRangeTime() throws ParseException {
        org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime startDate = DateTime.parse("2017-11-01 00:00:00", dtf);
        DateTime endDate = DateTime.parse("2017-11-02 23:59:59".replace(":59", ":00"), dtf);
        //
        int hours = Hours.hoursBetween(startDate, endDate).getHours();
        System.out.println(hours);

        List<DateTime> daysRange =
                Stream.iterate(startDate, date -> date.plusHours(1))
                        .limit(Hours.hoursBetween(startDate, endDate).getHours())
                        .collect(Collectors.toList());
        daysRange.add(endDate);
        for (DateTime date : daysRange) {
            System.out.println(dtf.print(date));
        }
    }

    @Test
    public void testRangeMonth() throws ParseException {
        LocalDate startDate = LocalDate.parse("2017-10-27 00:00:00", DateTimeFormat.forPattern("yyyy-MM-dd"));
        LocalDate endDate = LocalDate.parse("2017-11-24 11:00:00", DateTimeFormat.forPattern("yyyy-MM-dd"));
        org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM");
        List<LocalDate> daysRange =
                Stream.iterate(startDate, date -> date.plusMonths(1))
                        .limit(Months.monthsBetween(startDate, endDate).getMonths())
                        .collect(Collectors.toList());
        daysRange.add(endDate);
        for (LocalDate date : daysRange) {
            System.out.println(dtf.print(date));
        }
    }

    @Test
    public void testRangeMonthByNow() throws ParseException {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);
        org.joda.time.format.DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM");
        List<LocalDate> daysRange =
                Stream.iterate(startDate, date -> date.plusMonths(1))
                        .limit(Months.monthsBetween(startDate, endDate).getMonths())
                        .collect(Collectors.toList());
        daysRange.add(endDate);
        List<String> newList = daysRange.stream().map(dtf::print).collect(Collectors.toList());
        for (String str : newList) {
            System.out.println(str);
        }
        System.out.println(daysRange.stream().map(dtf::print).collect(Collectors.joining(",")));
    }

}
