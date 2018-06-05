package http;

import java.io.OutputStream;
import java.util.*;

import contract.Utils;
import dao.DAO;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by devlogic on 11/11/16.
 */
public class HttpsClient {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(HttpsClient.class);


    /**
     * POST request with body
     * @param url

     * @param body
     * @return
     * @throws Exception
     */
    public static String postRequest(String url,String body) throws Exception {
        HttpsURLConnection conn = createConnection(url,null);
        conn.setRequestMethod("POST");

        BufferedReader br = null;
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        if(body!=null){
            os.write(body.getBytes());
        }
        os.flush();
        os.close();
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 400) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            LOG.error("Error stream from service: " + getFromBuffer(br));
            throw new Exception("Error invoking service:" + conn.getResponseCode());
        }

        return getFromBuffer(br);
    }

    /**
     * POST request with query params ,headers and body
     * @param url
     * @param queryParams
     * @param header
     * @param body
     * @return
     * @throws Exception
     */
    public static String postRequest(String url, Map<String,Object> queryParams, Map<String,Object> header,String body) throws Exception {
        HttpsURLConnection conn = createConnection(url,queryParams);
        conn.setRequestMethod("POST");
        //add headers
        if(header!=null){
            for (Map.Entry<String, Object> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(),entry.getValue().toString());
            }
        }
        BufferedReader br = null;
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        if(body!=null){
            os.write(body.getBytes());
        }
        os.flush();
        os.close();
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 400) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            LOG.error("Error stream from service: " + getFromBuffer(br));
            throw new Exception("Error invoking service:" + conn.getResponseCode());
        }

       return getFromBuffer(br);
    }


    /**
     * POST request with query params and headers
     * @param url
     * @param queryParams
     * @param
     * @return
     * @throws Exception
     */
    public static String postRequest(String url, Map<String,Object> queryParams, Map<String,Object> header) throws Exception {
        HttpsURLConnection conn = createConnection(url,queryParams);
        conn.setRequestMethod("POST");
        //add headers
        if(header!=null){
            for (Map.Entry<String, Object> entry : header.entrySet()) {
                conn.setRequestProperty(entry.getKey(),entry.getValue().toString());
            }
        }
        BufferedReader br = null;
        OutputStream os = conn.getOutputStream();

        os.flush();
        os.close();
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 400) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        }
        else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            LOG.error("Error stream from service: " + getFromBuffer(br));
            throw new Exception("Error invoking service:" + conn.getResponseCode());
        }

        return getFromBuffer(br);
    }

    public static String postRequest(String url, String json, Properties prop) throws IOException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
        HttpsURLConnection conn = createConnection(url,null);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("username",prop.get("username").toString());
        conn.setRequestProperty("password",prop.get("password").toString());
        conn.setRequestProperty("apikey",prop.get("apikey").toString());
        conn.setDoOutput(true);
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();

        BufferedReader br = null;

        if(conn.getResponseCode() == 200) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        }else{
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String output;
        while((output = br.readLine()) != null)
        {
            sb.append(output);
        }

        br.close();

        String requestString = sb.toString();

        if(conn.getResponseCode() == 200)
            return requestString;

        LOG.error("error sending request");
        LOG.error(requestString);

        throw new RuntimeException("Error invoking service:" + conn.getResponseCode());

    }


    private static HttpsURLConnection createConnection(String urlString, Map<String,Object> params) throws NoSuchAlgorithmException, KeyManagementException, IOException, URISyntaxException {
        SSLContext ssl_ctx = SSLContext.getInstance("TLS");
        TrustManager[ ] trust_mgr = Utils.getTrustMgr();
        ssl_ctx.init(null,                // key manager
                trust_mgr,                // trust manager
                new SecureRandom());      // random number generator
        HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());

        URL url = buildUrl(urlString,params);
        LOG.info("url: " + url.toString());
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

        // Guard against "bad hostname" errors during handshake.
        con.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String host, SSLSession sess) {
                return true;
            }
        });

        return con;
    }

    private static URL buildUrl(String urlString, Map<String, Object> params) throws MalformedURLException, URISyntaxException {

        List<NameValuePair> nameValuePairList = new ArrayList<>();
        if(params!=null){
            Iterator it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                nameValuePairList.add(new BasicNameValuePair(pair.getKey().toString(), pair.getValue().toString()));
                it.remove();
            }
        }

        URI uri = new URIBuilder(urlString).addParameters(nameValuePairList).build();

        return uri.toURL();
    }


    /**
     *
     * @param br
     * @return
     * @throws IOException
     */
    private static String getFromBuffer(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String output;
        while((output = br.readLine()) != null)
        {
            sb.append(output);
        }

        br.close();

        return sb.toString();
    }

}
