package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;

@RestController
public class workingdayController {
    public static LocalDate ld = null;

    public static Boolean getText(String url, LocalDate d) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return (response.toString().contains(d.toString()));
    }

    @RequestMapping("/next-working-day")
    public LocalDate getLocalDate(@RequestParam(value = "after") String after) {
        DayOfWeek[] values = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        DayOfWeek[] nonWorkingDay = {DayOfWeek.SUNDAY, DayOfWeek.SATURDAY};


        if (after == null) {
            ld = LocalDate.now().plusDays(1);
            System.out.println(ld);
        }
        if (after != null) {

            ld = LocalDate.parse(after);
            ld = ld.plusDays(1);
        }


        boolean bankHolidayString;

        try {

            bankHolidayString = getText("https://www.gov.uk/bank-holidays.json", ld);

            Boolean contains = Arrays.stream(values).anyMatch(ld.getDayOfWeek()::equals);
            if (contains && bankHolidayString == true) {
                ld = ld.plusDays(1);
            }
            Boolean containsNonWorkingday = Arrays.stream(nonWorkingDay).anyMatch(ld.getDayOfWeek()::equals);
            Boolean isBankHoliday = false;
            while (isBankHoliday == false) {

                ld = ld.plusDays(1);
                bankHolidayString = getText("https://www.gov.uk/bank-holidays.json", ld);
                isBankHoliday = (Arrays.stream(values).anyMatch(ld.getDayOfWeek()::equals) && !bankHolidayString);

            }

        } catch (Exception e) {
            e.getMessage();
        }
        System.out.println(ld);
        return ld;

    }
}

