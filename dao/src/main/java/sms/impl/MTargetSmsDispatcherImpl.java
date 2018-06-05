package sms.impl;

import http.HttpsClient;
import sms.SmsDispatcher;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MTargetSmsDispatcherImpl implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(MTargetSmsDispatcherImpl.class);
    private final static String DISPATCHER_NAME ="MTarget SMS";

    public String getDispatcherName(){
        return DISPATCHER_NAME;
    }
    @Override
    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        String response = createHttpsRequest(receiver,message, properties);
        LOG.info("MTarget SMS response: " + response);
    }

    private String createHttpsRequest(String to, String message, Properties prop) throws Exception {
        String url = prop.getProperty("mtarget.sms.url");
        //create query params
        Map<String, Object> params = new HashMap<>();

        params.put("username",prop.getProperty("mtarget.sms.username"));
        params.put("password",prop.getProperty("mtarget.sms.password"));
        params.put("serviceId",prop.getProperty("mtarget.sms.serviceId"));
        params.put("msg",message);
        params.put("msisdn","00" + to);

        //send post request
        return HttpsClient.postRequest(url, params, null);
    }
}
