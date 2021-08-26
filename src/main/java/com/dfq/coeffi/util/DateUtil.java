package com.dfq.coeffi.util;


import com.dfq.coeffi.master.shift.Shift;
import com.dfq.coeffi.master.shift.ShiftRepository;
import com.dfq.coeffi.util.notification.messages.email.EmailConfig;
import com.dfq.coeffi.util.notification.messages.email.Mail;
import com.dfq.coeffi.util.notification.messages.email.MailService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateUtil implements Serializable {

    /**
     *
     */

    @Autowired
    private static MailService mailService;
    @Autowired
    private ShiftRepository shiftRepository;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat dformat = new SimpleDateFormat("yyyyMMdd");


    private static final long serialVersionUID = -3224601291474978013L;

    public static Date addMonthsToDate(int month) {
        Calendar calender = Calendar.getInstance();
        calender.add(Calendar.MONTH, month);
        Date dateAsObjectAfterMonth = calender.getTime();
        return dateAsObjectAfterMonth;
    }

    public static Date getTodayDate() {
        Date currentDate = null;
        String dateStr = "";
        String day = "";
        String month = "";
        int year = 0;

        Calendar cal = new GregorianCalendar();

        if (cal != null) {
            int dd = cal.get(Calendar.DAY_OF_MONTH);
            day = getDoubleDigits(dd);

            int mm = cal.get(Calendar.MONTH) + 1;
            month = getDoubleDigits(mm);

            year = cal.get(Calendar.YEAR);
        }

        dateStr = day + "/" + month + "/" + year;

        currentDate = convertToDate(dateStr);

        return currentDate;
    }

    public static int getMonthNumber(String monthName) {
        return Month.valueOf(monthName.toUpperCase()).getValue();
    }

    private static String getDoubleDigits(int inputValue) {
        String returnValue = "";

        if (inputValue < 10) {
            returnValue = "0" + inputValue;
        } else {
            returnValue = "" + inputValue;
        }

        return returnValue;
    }

    public static Date convertToDate(String date) {
        Date convertedDate = null;
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException pe) {
        }
        return convertedDate;
    }


    public static Date convertDateToFormat(Date date) {
        try {
            date = dformat.parse(dformat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date getDayBeforeYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        return cal.getTime();
    }

    public static String getDay(Date date) {
        return new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime());
    }

    public static String convertToDateString(Date date) {
        String convertedDate = null;

        if (date != null) {
            convertedDate = dateFormat.format(date);
        } else {
        }

        return convertedDate;
    }

    public static int calculateAge(Date birthDate, Date currentDate) {
        LocalDate birthLocal = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthLocal, currentLocalDate).getYears();
        } else {
            return 0;
        }
    }

    public static int getCurrentYear() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static String getCurrentMonth() {
        Date date = new Date();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        String monthName = monthFormat.format(date);
        return monthName;
    }

    public static Date getDateFromWeek(int week, int year) {
       /* int week = 3;
        int year = 2010;*/
        // Get calendar, clear it and set week number and year.
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.YEAR, year);

        // Now get the first day of week.
        Date date = calendar.getTime();
        return date;
    }

    public static Time getTimeFromString(String stringTime) throws ParseException {
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = timeFormat.format(stringTime);
        long formatedTime = timeFormat.parse(formattedDate).getTime();
        Time newTime = new Time(formatedTime);
        return newTime;
    }

    public static String getMonthName(Date inputDate) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
        String monthName = monthFormat.format(inputDate);
        return monthName;
    }

    public static int getMonthNumber(Date inputDate) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        String monthName = monthFormat.format(inputDate);
        int month = Integer.parseInt(monthName);
        return month;
    }

    public static int calculateDaysBetweenDate(Date fromDate, Date currentDate) {
        LocalDate birthLocal = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentLocalDate = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if ((fromDate != null) && (currentDate != null)) {
            return Period.between(birthLocal, currentLocalDate).getDays();
        } else {
            return 0;
        }
    }

    public static List<Date> getDaysBetweenDates(Date startdate, Date enddate) {
        List<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static List<Long> getDateBetweenDates(Date startdate, Date enddate) {
        List<Long> dateNumber = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate) || calendar.getTime().equals(enddate)) {
            Date result = calendar.getTime();
            String day = new SimpleDateFormat("dd").format(result);    // always 2 digits
            dateNumber.add(Long.valueOf(day));
            calendar.add(Calendar.DATE, 1);
        }
        return dateNumber;
    }

    public static int monthsBetween(Date d1, Date d2) {
        if (d2 == null || d1 == null) {
            return -1;//Error
        }
        Calendar m_calendar = Calendar.getInstance();
        m_calendar.setTime(d1);
        int nMonth1 = 12 * m_calendar.get(Calendar.YEAR) + m_calendar.get(Calendar.MONTH);
        m_calendar.setTime(d2);
        int nMonth2 = 12 * m_calendar.get(Calendar.YEAR) + m_calendar.get(Calendar.MONTH);
        return java.lang.Math.abs(nMonth2 - nMonth1);
    }

    public static int getRunningHour() {
        DateTime dateTime = new DateTime();
        int runningHour = dateTime.getHourOfDay();
        return runningHour;
    }

    public static int getRunningHour(Date date) {
        int runningHour = date.getHours();
        return runningHour;
    }

    public static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static void sendEmai(String email, String title, String content) {
        Date todayDate = new Date();
        EmailConfig emailConfignew = new EmailConfig();
        String emailTitle = title + " on " + todayDate;
        Mail mailnew = emailConfignew.setMailCredentials(email, emailTitle, content, null);
        mailService.sendEmail(mailnew, "****");
    }


    public static long getDifferenceMinutes(Date d1, Date d2) {
        long diffMs = d1.getTime() - d2.getTime();
        long diffSec = diffMs / 1000;
        long min = diffSec / 60;
        return min;
    }

    // Added by Dhwanit
    public static Date getMonthEndDate(int month, int year) {
        int[] daysInAMonth = {29, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int day = daysInAMonth[month];
        boolean isLeapYear = new GregorianCalendar().isLeapYear(year);

        if (isLeapYear && month == 2) {
            day++;
        }
        GregorianCalendar gc = new GregorianCalendar(year, month - 1, day);
        Date monthEndDate = new java.util.Date(gc.getTime().getTime());
        return monthEndDate;
    }

    public static String getDayOfDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String todayDate = dateFormat.format(date);
        return todayDate;
    }

    public static String getMonthNameOfDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM");
        String todayDate = dateFormat.format(date);
        return todayDate;
    }

    public static Date getDate(int day, int month, int year) {
        GregorianCalendar gc = new GregorianCalendar(year, month - 1, day);
        Date monthEndDate = new java.util.Date(gc.getTime().getTime());
        return monthEndDate;
    }

    public static java.util.Date calculateMonthEndDate(int month, int year) {
        int[] daysInAMonth = {29, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int day = daysInAMonth[month];
        boolean isLeapYear = new GregorianCalendar().isLeapYear(year);

        if (isLeapYear && month == 2) {
            day++;
        }
        GregorianCalendar gc = new GregorianCalendar(year, month - 1, day);
        java.util.Date monthEndDate = new java.util.Date(gc.getTime().getTime());
        return monthEndDate;
    }

    public static Date getStartDateOfMonth(int month, int year) {
        GregorianCalendar gc = new GregorianCalendar(year, month - 1, 1);
        java.util.Date startDate = new java.util.Date(gc.getTime().getTime());
        return startDate;
    }

    public ResponseEntity<Shift> getCurrentShift() {
        int currentTime = DateUtil.getRunningHour();
        System.out.println("CurrentTime " + currentTime);
        List<Shift> shiftList = new ArrayList<>();
        Shift shift = null;
        List<Shift> shifts = null;
        if (shifts == null) {
            shifts = shiftRepository.findAll();
        }
        for (Shift runningShift : shifts) {
            if (currentTime >= DateUtil.getRunningHour(runningShift.getStartTime()) && currentTime <= DateUtil.getRunningHour(runningShift.getEndTime())) {
                shiftList.add(runningShift);
            }
        }
        if (shiftList.isEmpty()) {
            return null;
        } else {
            return new ResponseEntity<>(shiftList.get(0), HttpStatus.OK);
        }
    }

    public static long getCalenderDays(int year, int month) {
        YearMonth yearMonthObject = YearMonth.of(year, month);
        int daysInMonth = yearMonthObject.lengthOfMonth();
        return daysInMonth;
    }

    public static Date yesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date dayBeforeyesterday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -2);
        return cal.getTime();
    }

    public static Date tomorrow(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date mySqlFormatDate() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date mySqlFormatDate(Date date1) {
        Date date = date1;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date startDateOfMonthAndYear(int year,int month){
        Calendar calendar = Calendar.getInstance();
        int day = 1;
        YearMonth yearMonth = YearMonth.of(year, month);

        calendar.set(year, month - 1, day);
        int numOfDaysInMonth = yearMonth.lengthOfMonth();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        Date endDate = calendar.getTime();
        return startDate;
    }

    public static Date endDateOfMonthAndYear(int year,int month){
        Calendar calendar = Calendar.getInstance();
        int day = 1;
        YearMonth yearMonth = YearMonth.of(year, month);

        calendar.set(year, month - 1, day);
        int numOfDaysInMonth = yearMonth.lengthOfMonth();
        Date startDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, numOfDaysInMonth - 1);
        Date endDate = calendar.getTime();
        return endDate;
    }
}