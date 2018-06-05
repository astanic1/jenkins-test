import contract.Response;
import dao.DAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

/**
 * Created by devlogic on 11/10/16.
 */
public class Main {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(Main.class);

    private static Properties props = new Properties();
    //15 seconds delay
    private static final int RUN_DELAY = 1500; //not likely to change no need for config

    public static void main(String[] args) throws IOException {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        props.load(loader.getResourceAsStream("default.properties"));

        String endpoint = props.getProperty("sqs.endpoint");




        AmazonSQS SQS = AmazonSQSClientBuilder.standard().build();
        //AmazonSQSClient sqsClientBuilder = new AmazonSQSClient(credentials).withRegion("us-west-2").build();
        //sqs.setRegion(Regions.US_WEST_2);
        //ListQueuesResult lq_result = sqs.listQueues();
        /*System.out.println("Your SQS Queue URLs:");
        for (String url : lq_result.getQueueUrls()) {
            System.out.println(url);



        }

        Map<String, MessageAttributeValue> attributes = new HashMap<String, MessageAttributeValue>();
        attributes.put("Name", new MessageAttributeValue()
                .withDataType("String")
                .withStringValue("Jane"));

        final SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.withMessageBody("This is my message text.");
        sendMessageRequest.withQueueUrl(endpoint);
        sendMessageRequest.withMessageAttributes(attributes);
        sqs.sendMessage(sendMessageRequest);


        sqs.sendMessage(new SendMessageRequest(endpoint, "This is my message text."));

        */

        //String reportingDbConnectionString = props.getProperty("jdbc.dlight.url");
        //String reportingDbUsername = props.getProperty("jdbc.dlight.username");
        //String reportingDdPassword = props.getProperty("jdbc.dlight.password");
        //DAO calculateDao = new DAO(reportingDbConnectionString,reportingDbUsername,reportingDdPassword);



        int errorCounter = 0;
        while(true)
        {
            AfricasTalkingTask africasTalkingTask =  new AfricasTalkingTask(SQS,props);
            try {
                LOG.info("sending Africas talking sms");
                Response response = africasTalkingTask.call();

                if (response.status == 200) {
                    if (errorCounter >= 10) {
                        LOG.fatal("sending Africa's talking sms successful");
                    }
                    LOG.info("success, previous error count: " + errorCounter);
                    errorCounter = 0;
                }
                else {
                    if (errorCounter == 9) {
                        LOG.fatal("sending Africa's talking sms failed");
                    }
                    LOG.info("error, previous error count: " + errorCounter);
                    errorCounter++;
                }

                LOG.info("DONE sending Africa's talking sms");
                LOG.info("Message: " + response.message);
                LOG.info("Code: " + response.status);

                Thread.sleep(RUN_DELAY);
            } catch (Exception e) {
                LOG.fatal("Thread for sending Africa's Talking SMSes stopped unexpectedly", e);
                Thread.currentThread().interrupt();
                break;
            }
        }

    }
}
