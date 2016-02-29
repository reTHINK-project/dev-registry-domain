package domainregistry;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;

public class Dates{
    public static String getActualDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(date);
    }

    public static long dateCompare(String actualDate, String date){
        long seconds = 12345678910L;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date date1 = sdf.parse(actualDate);
            Date date2 = sdf.parse(date);
            long duration  = date1.getTime() - date2.getTime();
            seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return seconds;
    }
}
