package team.project.Service;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

@SpringBootTest
@Transactional
class CalendarServiceTest {

    @Test
    void monthSchedule() {
        int year = 2022;
        int month = 2;
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year, month-1 ,1);
        int start = calendar.getActualMinimum(java.util.Calendar.DAY_OF_MONTH);
        int end = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        System.out.println(start);
        System.out.println(end);

        LocalDate startDate = LocalDate.of(year, month, start);
        LocalDate endDate = LocalDate.of(2022, month, end);
//
        System.out.println("startDate = "+ startDate);
        System.out.println("endDate = "+ endDate);

        int aa = LocalDate.now().getYear();
        int bb = LocalDate.now().getMonthValue();
        System.out.println("getYear = " + aa);
        System.out.println("getMonthValue = " + bb);

        String sourceReplace = "2022-02-23 15:19".replace(" ", "T");
        sourceReplace+=":00";
        System.out.println("LocalDateTime.parse(\\) = " + LocalDateTime.parse(sourceReplace));
    }

    @Test
    public void test(){
        String YYYY_MM_DD_HH_mm = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) (0[1-9]|1[0-9]|2[0-4]):(0[1-9]|[1-5][0-9])";
        String YYYY_MM_DD = "\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
        String source = "2022-02-10 09:00";
        String source2 = "2022-02-10 18:00";

        System.out.println("source.matches(YYYY_MM_DD) = " + source.matches(YYYY_MM_DD));
        System.out.println("source.matches(YYYY_MM_DD_HH_mm) = " + source.matches(YYYY_MM_DD_HH_mm));
        String sourceReplace = source.replace(" ", "T");
        String sourceReplace2 = source2.replace(" ", "T");
        System.out.println("sourceReplace = " + sourceReplace);
        System.out.println("LocalDateTime.parse() = " + LocalDateTime.parse(sourceReplace));
        System.out.println("sourceReplace = " + sourceReplace2);
        System.out.println("LocalDateTime.parse() = " + LocalDateTime.parse(sourceReplace2));
    }
}