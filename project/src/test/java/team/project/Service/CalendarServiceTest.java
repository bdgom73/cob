package team.project.Service;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    }
}