package sms.impl;

import org.apache.http.client.utils.URIBuilder;
import sms.SmsDispatcher;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by devlogic on 11/10/16.
 */
public class ConnectSmsDispatcherImpl implements SmsDispatcher{

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ConnectSmsDispatcherImpl.class);

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        String response = createHttpsRequest("0811621091",receiver,message, properties);
        LOG.info("Connect sms response: " + response);
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpsRequest(String from, String to, String message, Properties prop) throws IOException, URISyntaxException {
        URI uri = new URIBuilder(prop.getProperty("connect.sms.url"))
                .addParameter("from_number", from)
                .addParameter("username", prop.getProperty("connect.sms.username"))
                .addParameter("password", prop.getProperty("connect.sms.password"))
                .addParameter("destination", to)
                .addParameter("message", message)
                .build();

        HttpsURLConnection conn = (HttpsURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        //conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200)
        {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        String res = "";
        while ((output = br.readLine()) != null) {
            res = res + output;
        }

        conn.disconnect();
        return res;
    }
}
