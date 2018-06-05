package sms.impl;

import http.HttpsClient;
import sms.SmsDispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BudgetSmsDispatcherImpl  implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(BudgetSmsDispatcherImpl.class);
    private final static String DISPATCHER_NAME ="Budget SMS";

    public String getDispatcherName(){
        return DISPATCHER_NAME;
    }
    @Override
    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        String response = createHttpsRequest(receiver,message, properties);
        LOG.info("Budget SMS response: " + response);
    }

    private String createHttpsRequest(String to, String message, Properties prop) throws Exception {
        String url = prop.getProperty("budget.sms.url");
        //create query params
        Map<String, Object> params = new HashMap<>();
        params.put("username",prop.getProperty("budget.sms.username"));
        params.put("handle",prop.getProperty("budget.sms.handle"));
        params.put("userid",prop.getProperty("budget.sms.userid"));
        params.put("msg", message);
        params.put("from","BudgetSMS");
        params.put("to",to);

        //send post request
        return HttpsClient.postRequest(url, params, null);
    }


}
