package sms.impl;

import com.google.gson.Gson;
import contract.PNGSmsRequest;
import org.apache.http.client.utils.URIBuilder;
import sms.SmsDispatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by Kristina on 9/29/17.
 */
public class PNGSmsDispatcherImpl implements SmsDispatcher {
    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(PNGSmsDispatcherImpl.class);

    public void sendSmsToApi(String receiver, String message, Properties properties) throws IOException, URISyntaxException {
        String response = createHttpsRequest(receiver, message, properties);
        LOG.info(response);
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpsRequest(String to, String message, Properties prop) throws IOException, URISyntaxException {
        URI uri = new URIBuilder(prop.getProperty("png.sms.http.url"))
                .build();

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        PNGSmsRequest request = createRequest(to, message, prop);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();

        String jsonRequest = new Gson().toJson(request, PNGSmsRequest.class);
        LOG.info(jsonRequest);
        os.write(jsonRequest.getBytes());
        os.flush();
        os.close();

        BufferedReader br = null;

        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK && status != HttpURLConnection.HTTP_ACCEPTED
                && status != HttpURLConnection.HTTP_CREATED) {
            br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));

            String output;
            String res = "";
            while ((output = br.readLine()) != null) {
                res = res + output;
            }
            LOG.error(res);
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }

        br.close();
        return sb.toString();
    }

    /**
     * create request
     *
     * @param to
     * @param message
     * @param prop
     * @return
     */
    private PNGSmsRequest createRequest(String to, String message, Properties prop) {
        PNGSmsRequest request = new PNGSmsRequest();
        request.setUsername(prop.getProperty("png.sms.username"));
        request.setPassword(prop.getProperty("png.sms.password"));
        request.setMessage(message);
        request.setMSISDN(new String[]{to});
        return request;
    }
}