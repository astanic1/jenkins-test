package contract;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by devlogic on 3/7/16.
 */
public class Utils {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(Utils.class);

    public static String createMpesaRequestDate(Date date) throws ParseException {
        DateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(date);
    }
    public static Date subtractDaysFromDate(Date date,int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    public static Date subtractMonthFromDate(Date date,int months){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -months);

        return cal.getTime();
    }

    public static Date addDaysToDate(Date date,int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
    public static Date cvtToGmt( Date date ){
        TimeZone tz = TimeZone.getDefault();
        Date ret = new Date( date.getTime() - tz.getRawOffset() );

        // if we are now in DST, back off by the delta.  Note that we are checking the GMT date, this is the KEY.
        if ( tz.inDaylightTime( ret )){
            Date dstDate = new Date( ret.getTime() - tz.getDSTSavings() );

            // check to make sure we have not crossed back into standard time
            // this happens when we are on the cusp of DST (7pm the day before the change for PDT)
            if ( tz.inDaylightTime( dstDate )){
                ret = dstDate;
            }
        }
        return ret;
    }
    public static TrustManager[ ] getTrustMgr() {
        TrustManager[ ] certs = new TrustManager[ ] {
                new X509TrustManager() {
                    public X509Certificate[ ] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[ ] certs, String t) { }
                    public void checkServerTrusted(X509Certificate[ ] certs, String t) { }
                }
        };
        return certs;
    }
    public static Date fromStringToDate(String date)  {
        if(date==null) return null;
        DateFormat formatter ;
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Calendar createCurrentCalanderTime(){
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.HOUR_OF_DAY, 0);

        return now;
    }


    public static String metropolDate(Date curDate){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss500000");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(curDate);
    }

    public static String getMonthNameFromDate(Date date){
        return new SimpleDateFormat("MMM").format(date.getTime());
    }




    public static int getDaysBetweenTwoDates(Timestamp timestamp1, Timestamp timestamp2){
        return (int)( (timestamp2.getTime() - timestamp1.getTime()) / (1000 * 60 * 60 * 24));
    }


    public static Date fromStringToDateWithTime(String date)  {
        if(date==null) return null;
        DateFormat formatter ;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(date);
        } catch (ParseException e) {

            return null;
        }
    }
    public static String fromDateToString(Date date){
        if(date==null) return null;
        DateFormat formatter ;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try{
            return formatter.format(date);
        }catch(Exception e){

            return null;
        }
    }

    public static String fromDateToStringWithoutTime(Date date){
        if(date==null) return null;
        DateFormat formatter ;
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        try{
            return formatter.format(date);
        }catch(Exception e){

            return null;
        }
    }

    public static String cleanUpCommasForCsv(String value)
    {
        if(value == null)
            return "";

        if(value.contains(","));
        value = value.replace(",", " ");

        return value;
    }


    public static String getReportDateformat(Date createdAt) {
        DateFormat formatter ;
        formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        try{
            return formatter.format(createdAt);
        }catch(Exception e){

            return null;
        }
    }
    public static String addStartTime(String dateString){
        if(dateString==null) return null;
        return dateString + " 00:00:00";
    }
    public static String addEndTime(String dateString){
        if(dateString==null) return null;
        return dateString + " 23:59:59";
    }

    public static Date getFirstDateOfTheMonth(Date date) {
        Calendar c = Calendar.getInstance();   // this takes current date
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);

        return c.getTime();
    }

    public static Date getLastDateOfTheMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

        return c.getTime();
    }

    public static int getMonthNumberFromDate(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        return calendar.getActualMaximum(Calendar.DATE);
    }

    public static boolean isEmpty(Object[] arr){
        if(arr==null)
            return true;
        boolean empty = true;
        for (int i=0; i<arr.length; i++) {
            if (arr[i] != null) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    public static String fromDateToString(Date date, String format){
        if(date==null) return null;
        DateFormat formatter ;
        formatter = new SimpleDateFormat(format);
        return formatDate(date,formatter);
    }

    private static String formatDate(Date date, DateFormat formatter)
    {
        try{
            return formatter.format(date);
        }catch(Exception e){
            if (LOG.isDebugEnabled())
                LOG.debug("failed parsing date", e);
            return null;
        }
    }
}
