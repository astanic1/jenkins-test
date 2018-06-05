package sms.impl;

import http.HttpsClient;
import sms.SmsDispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LmtGroupSmsDispatcherImpl  implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(LmtGroupSmsDispatcherImpl.class);
    private final static String DISPATCHER_NAME ="LMT Group SMS";

    public String getDispatcherName(){
        return DISPATCHER_NAME;
    }
    @Override
    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        String response = createHttpsRequest(receiver,message, properties);
        LOG.info("LMT Group SMS response: " + response);
    }

    private String createHttpsRequest(String to, String message, Properties prop) throws Exception {
        String url = prop.getProperty("lmtgroup.sms.url");
        //create query params
        Map<String, Object> header = new HashMap<>();
        header.put("Content-Type","application/x-www-form-urlencoded");


        //api_key=3tJlOze9mU85Fuo&password=TOT@1Cam&message=test&sender=test&phone=237694124344
        String body = "api_key=" + prop.getProperty("lmtgroup.sms.apikey") + "&" +
                "password=" + prop.getProperty("lmtgroup.sms.password") + "&" +
                "message=" + message + "&" +
                "sender=" + "test" + "&" +
                "phone=" + to;
        return HttpsClient.postRequest(url, null,header, body);
    }


}
