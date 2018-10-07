package co.ppk.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class test  {
    public static void main(String[] args) throws Exception {
        //aplico reglas
        String string = "2018-02-15T00:00:00";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = format.parse(string);
        System.out.println(date);

        Date startDate = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        //agrego nDays and defined end date
        cal.add(Calendar.DAY_OF_YEAR, 5);
        cal.clear(Calendar.ZONE_OFFSET);
        Date endDate = cal.getTime();
        System.out.println("end date: " + endDate);

        //create start
        cal.setTime(startDate);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.clear(Calendar.ZONE_OFFSET);
        startDate = cal.getTime();
        System.out.println("start date: " + startDate);
    }

}
