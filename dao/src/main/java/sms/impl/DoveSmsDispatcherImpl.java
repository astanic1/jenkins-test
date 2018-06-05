package sms.impl;

import org.apache.http.client.utils.URIBuilder;
import sms.SmsDispatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * Created by Kristina on 3/8/17.
 */
public class DoveSmsDispatcherImpl implements SmsDispatcher {

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        createHttpRequest(receiver, message, properties);
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpRequest(String to, String message, Properties prop) throws IOException, URISyntaxException, NoSuchAlgorithmException {

        //boolean isAllASCII = message.chars().allMatch(c -> c < 128);

        URI uri = new URIBuilder(prop.getProperty("dove.sms.url"))
                .addParameter("user", prop.getProperty("dove.sms.username"))
                .addParameter("key", prop.getProperty("dove.sms.apikey"))
                .addParameter("mobile", to)
                .addParameter("message", message)
                .addParameter("accusage", prop.getProperty("dove.sms.accusage"))
                .addParameter("senderid", prop.getProperty("dove.sms.senderid"))
                .addParameter("unicode", "1")
                .build();

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
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

        if (!res.isEmpty() && res.startsWith("fail")) {
            throw new RuntimeException("Failed : Response message : "
                    + res);
        }

        return res;
    }
}