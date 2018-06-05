package sms.impl;

import sms.SmsDispatcher;
import sms.impl.external.smsgh.*;

import java.util.Properties;

/**
 * Created by devlogic on 11/10/16.
 */
public class SmsGhDispatcherImpl implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(SmsGhDispatcherImpl.class);

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        BasicAuth auth = new BasicAuth(properties.getProperty("smsgh.sms.username"),
                properties.getProperty("smsgh.sms.password"));
        ApiHost host = new ApiHost(auth);
        // Instance of the Messaging API
        MessagingApi messagingApi = new MessagingApi(host);

        MessageResponse response = messagingApi.sendQuickMessage(properties.getProperty("smsgh.sms.shortcode"), receiver, message, null);

        LOG.info("SMS response: " + response.getStatus() + "description: " + response.getDetail());
    }

    @Override
    public String getDispatcherName() {
        return null;
    }
}
