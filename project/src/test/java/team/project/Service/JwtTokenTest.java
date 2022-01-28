package team.project.Service;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenTest {

    @Test
    void createToken() {
    }

    @Test
    void extractMemberIdFromToken() {
    }

    @Test
    void extractToken() {

        Date date1 = new Date();

        Date date2 = new Date(date1.getTime() + 360000);

        System.out.println("date1 = " + date1);
        System.out.println("date2 = " + date2);
        boolean before = date1.before(date1);
        System.out.println("before = " + before);
    }
}