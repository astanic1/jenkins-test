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
 * Created by Kristina on 3/16/17.
 */
public class SmsProDispatcherImpl implements SmsDispatcher {

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        createHttpRequest(receiver, message, properties);
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpRequest(String to, String message, Properties prop) throws IOException, URISyntaxException, NoSuchAlgorithmException {

        URI uri = new URIBuilder(prop.getProperty("smspro.endpoint.http.url"))
                .addParameter("customerID", prop.getProperty("smspro.endpoint.http.customer")) //3650
                .addParameter("userName", prop.getProperty("smspro.endpoint.http.username")) //ad3d87f731XX
                .addParameter("userPassword", prop.getProperty("smspro.endpoint.http.password"))
                .addParameter("originator", prop.getProperty("smspro.endpoint.http.sender"))
                .addParameter("messageType", "ArabicWithLatinNumbers")
                .addParameter("blink", "false")
                .addParameter("flash", "false")
                .addParameter("Private", "false")
                .addParameter("defDate", "")
                .addParameter("recipientPhone", to)
                .addParameter("smsText", message)
                .build();


        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getErrorStream())));

            String output;
            String res = "";
            while ((output = br.readLine()) != null) {
                res = res + output;
            }

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

        conn.disconnect();
        return res;
    }
}