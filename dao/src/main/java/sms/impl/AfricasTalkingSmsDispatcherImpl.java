package sms.impl;

import sms.SmsDispatcher;

import java.util.Properties;

/**
 * Created by devlogic on 11/9/16.
 */
public class AfricasTalkingSmsDispatcherImpl implements SmsDispatcher {

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {

            AfricasTalkingGateway gateway = new AfricasTalkingGateway(properties.get("africas.talking.username").toString(),
                    properties.get("africas.talking.apikey").toString(),
                    properties.get("africas.talking.endpoint").toString());
            gateway.sendMessage(receiver,message,properties.get("africas.talking.sender").toString());
    }

    @Override
    public String getDispatcherName() {
        return null;
    }
}
