package sms;

import java.util.Properties;

/**
 * Created by devlogic on 11/9/16.
 */
public interface SmsDispatcher {

    /**
     * SMS interface
     * @param receiver
     * @param message
     */
   void sendSmsToApi(String receiver, String message, Properties properties) throws Exception;

    /**
     * Get dispatcher name i.e. Budget SMS
     * @return
     */
    String getDispatcherName();
}
