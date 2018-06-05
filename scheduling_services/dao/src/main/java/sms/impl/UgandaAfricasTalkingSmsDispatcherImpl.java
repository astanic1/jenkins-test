package sms.impl;

import sms.SmsDispatcher;

import java.util.Properties;

/**
 * Created by devlogic on 12/21/16.
 */
public class UgandaAfricasTalkingSmsDispatcherImpl implements SmsDispatcher {
    @Override
    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {

            AfricasTalkingGateway gateway = new AfricasTalkingGateway(properties.get("uganda.africas.talking.username").toString(),
                    properties.get("uganda.africas.talking.apikey").toString(),
                    properties.get("uganda.africas.talking.endpoint").toString());
            gateway.sendMessage(receiver,message, properties.get("uganda.africas.talking.sender").toString());
    }

    @Override
    public String getDispatcherName() {
        return null;
    }
}
