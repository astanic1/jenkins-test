package sms;

import sms.impl.SmsProcessorImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class BaseSmsProcessor  {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(BaseSmsProcessor.class);
    /**
     * Dynamically build delete String for SmsQueue;
     *
     * @param delete
     * @return
     */
    protected String generateDeleteString(Object[] delete) {
        StringBuilder sb = new StringBuilder();
        String sql = "DELETE FROM SmsQueue WHERE SmsQueueId IN ( ";
        sb.append(sql);
        int i = 0;
        for (Object del : delete) {
            sb.append(del);
            if (i < delete.length - 1)
                sb.append(", ");
            i++;
        }
        sb.append(" );");
        return sb.toString();
    }

    /**
     * Inserts all sms messages as they are delivered. Due to high impact and low priority this method must not
     * fail SMS processing //TODO remove try catch once proven stable.
     *
     * @param logs
     * @return
     */
    protected static String generateInsertLogString(List<Map<String, Object>> logs) {
        try {
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat toQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeString = toQuery.format(new Date());

            sb.append(INSERT_IN_SMS_LOG);

            for (Map<String, Object> log : logs) {
                sb.append(String.format("('%s','%s',%d,%d),", log.get("Message"), timeString, (Integer) log.get("PaymentTransactionId"), (Integer) log.get("CustomerId")));
            }

            sb.delete(sb.length() - 1, sb.length());

            return sb.toString();

        } catch (Exception ex) {
            LOG.error("Error saving SMS Log", ex);
        }

        return null;
    }

    //SQL
    protected static final String GET_ALL_SMS_BY_COUNTRY = "SELECT sq.SmsQueueId, sq.Message, sq.SmsPhoneNumber, sq.CountryId FROM SmsQueue sq join Country c on c.CountryId = sq.CountryId where c.CountryName = ?";
    protected static String INSERT_IN_SMS_LOG = "insert into SmsLog (Message,DateDelivered,PaymentTransactionId,CustomerId) values ";
}
