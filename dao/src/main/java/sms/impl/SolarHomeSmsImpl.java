package sms.impl;

import com.google.gson.Gson;
import contract.SolarHomeRequest;
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

public class SolarHomeSmsImpl implements SmsDispatcher {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(SolarHomeSmsImpl.class);

    @Override
    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        String response = createHttpsRequest(receiver, message, properties);
        LOG.info(response);
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpsRequest(String to, String message, Properties prop) throws IOException, URISyntaxException {
        URI uri = new URIBuilder(prop.getProperty("solarhome.sms.http.url"))
                .build();

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        SolarHomeRequest request = createRequest(to, message, prop);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();

        String jsonRequest = new Gson().toJson(request, SolarHomeRequest.class);
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
    private SolarHomeRequest createRequest(String to, String message, Properties prop) {
        SolarHomeRequest request = new SolarHomeRequest();
        request.setApi_key(prop.getProperty("solarhome.sms.apikey"));
        request.setContent(message);
        request.setNumber(to);
        return request;
    }
}
