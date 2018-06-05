package sms.impl;

import com.google.gson.Gson;
import contract.IgniteRequest;
import org.apache.http.client.utils.URIBuilder;
import sms.SmsDispatcher;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by devlogic on 11/28/16.
 */
public class IgniteSmsDispatcherImpl implements SmsDispatcher {
    public void sendSmsToApi(String receiver, String message, Properties properties) {
        try {
            String response = createHttpsRequest("Ignite",receiver,message, properties);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private String createHttpsRequest(String from, String to, String message, Properties prop) throws IOException, URISyntaxException {
        URI uri = new URIBuilder(prop.getProperty("ignite.sms.url"))
                .build();

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        //conn.setRequestProperty("Accept", "application/json");
        IgniteRequest igniteRequest = createIgniteRequest(from, to, message, prop);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("signature",prop.get("ignite.sms.signature").toString());
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(new Gson().toJson(igniteRequest,IgniteRequest.class).getBytes());
        os.flush();
        os.close();

        BufferedReader br = null;

        if(conn.getResponseCode() != 200) {
            throw new RuntimeException("Error invoking service:" + conn.getResponseCode());
        }
        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String output;
        while((output = br.readLine()) != null)
        {
            sb.append(output);
        }

        br.close();

        String requestString = sb.toString();

        return requestString;
    }

    /**
     * create ignite request
     * @param from
     * @param to
     * @param message
     * @param prop
     * @return
     */
    private IgniteRequest createIgniteRequest(String from, String to, String message, Properties prop) {
        IgniteRequest igniteRequest = new IgniteRequest();
        igniteRequest.setSrc(from);
        igniteRequest.setDest(to);
        igniteRequest.setContractId(Integer.valueOf(prop.getProperty("ignite.sms.contract.id")));
        igniteRequest.setMessage(message);
        igniteRequest.setWait(0);

        return igniteRequest;
    }
}
