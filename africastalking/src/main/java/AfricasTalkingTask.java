import com.amazonaws.services.sqs.model.*;
import contract.Response;
import dao.DAO;
import sms.impl.AfricasTalkingSmsDispatcherImpl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.Callable;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


public class AfricasTalkingTask implements Callable<Response> {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(AfricasTalkingTask.class);
    private AmazonSQS _sqs;
    private Properties _props;

    public AfricasTalkingTask(AmazonSQS sqs, Properties props) {
        this._sqs = sqs;
        this._props = props;
    }

    @Override
    public Response call() throws Exception {
        Response response = new Response();
        //get this by country
        AfricasTalkingSmsDispatcherImpl africasTalkingSmsDispatcher = new AfricasTalkingSmsDispatcherImpl();
        try {

            String endpoint = _props.getProperty("sqs.endpoint");
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(endpoint);
            receiveMessageRequest.withMessageAttributeNames("All");
            List<Message> smsList = _sqs.receiveMessage(receiveMessageRequest).getMessages();



            boolean hadErrors = false;
            if(smsList.size()>0){

                List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();


                String[] delete = new String[smsList.size()];
                List<Message> logInsertObjects = new ArrayList<>();
                int i = 0;

                for(Message message: smsList){

                    System.out.println("poruka ");

                    Map<String,MessageAttributeValue> attributes = message.getMessageAttributes();
                    try{
                        delete[i] = message.getReceiptHandle();
                        i++;
                        logInsertObjects.add(message);

                        entries.add(
                                new DeleteMessageBatchRequestEntry()
                                        .withId(message.getMessageId())
                                        .withReceiptHandle(message.getReceiptHandle()));


                        LOG.info("Sending " + message.getMessageId() +" to " + attributes.get("SmsPhoneNumber").getStringValue()
                                + " : " + message.getBody());


                        System.out.println("Sending " + message.getMessageId() +" to " + attributes.get("SmsPhoneNumber").getStringValue()
                                + " : " + message.getBody());

                       /*
                        africasTalkingSmsDispatcher.sendSmsToApi(map.get("SmsPhoneNumber").toString(),
                                map.get("Message").toString(),
                                _props);
                                */
                    }catch(Exception e){
                        //LOG.info("Failed to send " + message.getMessageId() +" to " + attributes.get("SmsPhoneNumber").getStringValue() + " : " +
                        e.printStackTrace();
                        hadErrors = true;
                    }
                }
                try {

                    //generateInsertLogString(logInsertObjects);
                    deleteMessages(entries);

                }catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            if (hadErrors) {
                response.message = "Some messages failed for Africa's Talking";
                response.status = 500;
            }
            else {
                response.message = "Sent Africa's Talking SMSes";
                response.status = 200;
            }
        } catch (Exception e) {
            LOG.error("Error getting response",e);
            response.status = 500;
            response.message = e.getMessage();
        }

        return response;
    }

    public void deleteMessages(List<DeleteMessageBatchRequestEntry> entries)
    {
        DeleteMessageBatchRequest deleteMessageBatchRequest = new DeleteMessageBatchRequest();
        deleteMessageBatchRequest.setEntries(entries);
        deleteMessageBatchRequest.withQueueUrl(_props.getProperty("sqs.endpoint"));
        _sqs.deleteMessageBatch(deleteMessageBatchRequest);
    }



    private void generateInsertLogString(List<Message> logs)
    {
        try{
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat toQuery = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeString = toQuery.format(new Date());

            sb.append(insertInSmsLog);

            for(Message log : logs)
            {
                sb.append(String.format("('%s','%s',%d,%d,%d),",log.getBody(),timeString,Integer.parseInt(log.getMessageAttributes().get("PaymentTransactionId").getStringValue())
                        ,Integer.parseInt(log.getMessageAttributes().get("CustomerId").getStringValue())
                        ,Integer.parseInt(log.getMessageAttributes().get("CountryId").getStringValue())));
            }

            sb.delete(sb.length()-1,sb.length());

            //_dao.executeUpdate(sb.toString(),null);

        }catch(Exception ex)
        {
            LOG.error("Error saving SMS Log",ex);
        }
    }




    //SQL
    static final String insertInSmsLog = "insert into SmsLog (Message,DateDelivered,PaymentTransactionId,CustomerId,CountryId) values ";

}
