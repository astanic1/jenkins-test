package sms.impl;
/**********************************************************************************************************************
 * # COPYRIGHT (C) 2014 AFRICASTALKING LTD <www.africastalking.com>                                                   *
 **********************************************************************************************************************
 *AFRICAStALKING SMS GATEWAY CLASS IS A FREE SOFTWARE IE. CAN BE MODIFIED AND/OR REDISTRIBUTED                        *
 *UNDER THER TERMS OF GNU GENERAL PUBLIC LICENCES AS PUBLISHED BY THE                                                 *
 *FREE SOFTWARE FOUNDATION VERSION 3 OR ANY LATER VERSION                                                             *
 **********************************************************************************************************************
 *THE CLASS IS DISTRIBUTED ON 'AS IS' BASIS WITHOUT ANY WARRANTY, INCLUDING BUT NOT LIMITED TO                        *
 *THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.                      *
 *IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,             *
 *WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE        *
 *OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.                                                                       *
 **********************************************************************************************************************/

import com.google.gson.Gson;
import dao.DAO;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

public class AfricasTalkingGateway
{
	private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AfricasTalkingGateway.class);

	private String _username;
    private String _apiKey;
    private int responseCode;
    private String SMSURLString;

	private static final int HTTP_CODE_OK         = 200;
	private static final int HTTP_CODE_CREATED    = 201;
	
	//Change debug flag to true to view raw server response
	private static final boolean DEBUG = false;
	
	public AfricasTalkingGateway(String username_, String apiKey_, String smsUrlString_)
    {
		_username = username_;
		_apiKey   = apiKey_;
		SMSURLString = smsUrlString_;
    }
    
	
    public void sendMessage(String to_, String message_, String sender_) throws Exception
    {
    
    	HashMap<String, String> data = new HashMap<String, String>();
    	data.put("username", _username);
    	data.put("to", to_);
    	data.put("message", message_);
	    data.put("from",sender_);
    	sendMessageImpro(to_, message_, data);
    }

    private void sendMessageImpro(String to_, String message_, HashMap<String, String> data_) throws Exception {
		/*Object[] insert = new Object[4];
		insert[0] = "AFRICASTALKING";
		insert[1] = new Timestamp(System.currentTimeMillis());*/
		String response = sendPOSTRequest(data_, SMSURLString);
		LOG.info("SMS Response: " + response);
		/*try{
			//remove this later
			Properties props = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			props.load(loader.getResourceAsStream("default.properties"));

			String reportingDbConnectionString = props.getProperty("jdbc.dlight.url");
			String reportingDbUsername = props.getProperty("jdbc.dlight.username");
			String reportingDdPassword = props.getProperty("jdbc.dlight.password");
			DAO calculateDao = new DAO(reportingDbConnectionString,reportingDbUsername,reportingDdPassword);
			Response resp = new Gson().fromJson(response,Response.class);
			String INSERT_INTO_LOG = "INSERT INTO BmobileConLog(Message,start,end, Status) values(?, ?, ?, ?)";
			insert[2] = new Timestamp(System.currentTimeMillis());
			insert[3] = resp.getSMSMessageData().getRecipients().get(0).getStatus();
			calculateDao.executeInsert(INSERT_INTO_LOG,insert);
		}catch(Exception e){
			e.printStackTrace();
		}*/
    	if (responseCode == HTTP_CODE_CREATED || responseCode == HTTP_CODE_OK) {
			return;
    	}

    	throw new Exception(response);
    }

    private String sendPOSTRequest(HashMap<String, String> dataMap_, String urlString_) throws Exception {
    	try {
    		String data = new String();
    		Iterator<Entry<String, String>> it = dataMap_.entrySet().iterator();
    		while (it.hasNext()) {
    			Entry<String, String> pairs = (Entry<String, String>)it.next();
    			data += URLEncoder.encode(pairs.getKey().toString(), "UTF-8");
    			data += "=" + URLEncoder.encode(pairs.getValue().toString(), "UTF-8");
    			if ( it.hasNext() ) data += "&";
    		}
    		URL url = new URL(urlString_);
    		URLConnection conn = url.openConnection();
    		conn.setRequestProperty("Accept", "application/json");
    		conn.setRequestProperty("apikey", _apiKey);
	    	conn.setDoOutput(true);
	    	OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	    	writer.write(data);
	    	writer.flush();
	    
	    	HttpURLConnection http_conn = (HttpURLConnection)conn;
	    	responseCode = http_conn.getResponseCode();
			
	    	BufferedReader reader;
    		if(responseCode == HTTP_CODE_OK || responseCode == HTTP_CODE_CREATED)
    			reader = new BufferedReader(new InputStreamReader(http_conn.getInputStream()));
    		else
    			reader = new BufferedReader(new InputStreamReader(http_conn.getErrorStream()));
    		String response = reader.readLine();
            reader.close();
    		return response;
	    
    	} catch (Exception e){
    		throw e;
 		}
    }

    private String sendGETRequest(String urlString) throws Exception
    {
    	try {
    		URL url= new URL(urlString);
    		URLConnection connection = (URLConnection)url.openConnection();
    		connection.setRequestProperty("Accept","application/json");
    		connection.setRequestProperty("apikey", _apiKey);
    		
    		HttpURLConnection http_conn = (HttpURLConnection)connection;
    		responseCode = http_conn.getResponseCode();
    		
    		BufferedReader reader;
    		if(responseCode == HTTP_CODE_OK || responseCode == HTTP_CODE_CREATED)
    			reader = new BufferedReader(new InputStreamReader(http_conn.getInputStream()));
    		else
    			reader = new BufferedReader(new InputStreamReader(http_conn.getErrorStream()));
    		String response = reader.readLine();
    		
    		if(DEBUG)
    			System.out.println(response);
    		
    		reader.close();
    		return response;
    	}
    	catch (Exception e) {throw e;}
    }
}