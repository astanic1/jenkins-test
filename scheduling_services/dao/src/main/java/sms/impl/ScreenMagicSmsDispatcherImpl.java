package sms.impl;

import org.apache.http.client.utils.URIBuilder;
import sms.SmsDispatcher;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * Created by root on 12/22/16.
 */
public class ScreenMagicSmsDispatcherImpl implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(ScreenMagicSmsDispatcherImpl.class);

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        createHttpsRequest(receiver, message, properties);
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpsRequest(String to, String message, Properties prop) throws IOException, URISyntaxException, NoSuchAlgorithmException {
        URI uri = new URIBuilder(prop.getProperty("screen.magic.url"))
                .addParameter("userid", prop.getProperty("screen.magic.userid"))
                .addParameter("accountid", prop.getProperty("screen.magic.accountid"))
                .addParameter("to", to)
                .addParameter("senderid", prop.getProperty("screen.magic.senderid"))
                .addParameter("hashkey", generateHashKey(prop.getProperty("screen.magic.userid"),
                        prop.getProperty("screen.magic.password"),
                        prop.getProperty("screen.magic.accountid"),
                        prop.getProperty("screen.magic.senderid"),
                        to,
                        message))
                .addParameter("msg", message)
                .build();

        HttpsURLConnection conn = (HttpsURLConnection) uri.toURL().openConnection();
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

        if (res.startsWith("error")) {
            LOG.error(res);
        }

        conn.disconnect();
        return res;
    }

    //generate md5 hash of concatenation of userid, password, accountid, senderid, mobilenumber and msg
    private static String generateHashKey(String userid, String password, String accountid, String senderid,
                                          String to, String message) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String hashInput = userid + password + accountid + senderid + to + message;

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(StandardCharsets.UTF_8.encode(hashInput));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }
}