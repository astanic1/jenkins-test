package sms.impl;

import com.google.gson.Gson;
import contract.GikkoSmsRequest;
import contract.GikkoSmsResponse;
import http.HttpsClient;
import sms.SmsDispatcher;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GikkoSmsDispatherImpl implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(LmtGroupSmsDispatcherImpl.class);
    private final static String DISPATCHER_NAME ="Gikko SMS";

    public String getDispatcherName(){
        return DISPATCHER_NAME;
    }
    @Override
    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        String response = createHttpsRequest(receiver,message, properties);
         GikkoSmsResponse gikkoSmsResponse = new Gson().fromJson(response, GikkoSmsResponse.class);

         GikkoSmsResponse.Messages gikkoMessage = null;
         if(gikkoSmsResponse.getMessages().size()>0){
             gikkoMessage = gikkoSmsResponse.getMessages().get(0);
         }
         if(gikkoMessage!=null){
             LOG.info("Gikko SMS response status: " + gikkoMessage.getStatus().getGroupName());
             LOG.info("Gikko SMS response messageId: " + gikkoMessage.getMessageId());

             if(!gikkoMessage.getStatus().getGroupName().equals("PENDING")){
                 LOG.error("Error sending message to Gikko sms:");
                 LOG.error("Status: " + gikkoMessage.getStatus().getId());
                 LOG.error("status group: " + gikkoMessage.getStatus().getGroupName());
                 LOG.error("description: " + gikkoMessage.getStatus().getDescription());

                 throw new Exception("Could not send sms to Gikko: " + gikkoMessage.getStatus().getDescription());
             }
         }else{
             LOG.warn("No response from Gikko SMS");
         }

    }

    private String createHttpsRequest(String to, String message, Properties prop) throws Exception {
        String url = prop.getProperty("gikko.sms.url");
        //create query params
        Map<String, Object> header = new HashMap<>();
        String credential = Base64.getEncoder().encodeToString((prop.getProperty("gikko.sms.username") + ":"
        +prop.getProperty("gikko.sms.password")).getBytes());
        header.put("Content-Type","application/json");
        header.put("Authorization","Basic " + credential);


        GikkoSmsRequest gikkoSmsRequest = new GikkoSmsRequest();
        gikkoSmsRequest.setTo(to);
        gikkoSmsRequest.setText(message);
        //send post request
        return HttpsClient.postRequest(url, null,header, new Gson().toJson(gikkoSmsRequest));
    }

}
