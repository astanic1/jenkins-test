package sms.impl;

import org.apache.http.client.utils.URIBuilder;
import sms.SmsDispatcher;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by Kristina on 5/11/17.
 */
public class MtechSmsDispatcherImpl implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(MtechSmsDispatcherImpl.class);

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        createHttpsRequest(receiver,message, properties);
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpsRequest(String to, String message, Properties prop) throws IOException, URISyntaxException {
        URI uri = new URIBuilder(prop.getProperty("mtech.sms.url"))
                .addParameter("shortCode", prop.getProperty("mtech.sms.shortcode"))
                .addParameter("user", prop.getProperty("mtech.sms.username"))
                .addParameter("pass", prop.getProperty("mtech.sms.password"))
                .addParameter("MSISDN", to)
                .addParameter("MESSAGE", message)
                .build();

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");
        //conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getErrorStream())));

            String output;
            String res = "";
            while ((output = br.readLine()) != null) {
                res = res + output;
            }

            LOG.info("SMS Response: " + res);
            throw new RuntimeException("Failed : " + res + " HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        String res = "";
        while ((output = br.readLine()) != null) {
            res = res + output;
        }

        LOG.info("SMS Response: " + res);

        conn.disconnect();

        return res;
    }
}