package sms.impl;

import contract.Response;
import contract.Utils;
import dao.DAO;
import factory.AbstractFactory;
import factory.FactoryMaker;
import sms.BaseSmsProcessor;
import sms.SmsDispatcher;
import sms.SmsProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class SmsProcessorImpl extends BaseSmsProcessor implements SmsProcessor {

    private final static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger(SmsProcessorImpl.class);
    private DAO _dao;
    private Properties _props;

    public SmsProcessorImpl(DAO _dao, Properties _props) {
        this._dao = _dao;
        this._props = _props;
    }

    @Override
    public Response processSms(String countryName) {
        Response response = new Response();
        try {
            AbstractFactory abstractFactory = FactoryMaker.getFactory("sms");
            SmsDispatcher smsDispatcher = abstractFactory.getSmsDispatcher(countryName);
            Object[] params = new Object[1];
            params[0] = countryName;
            List<Map<String, Object>> smsList = _dao.executeQuery(GET_ALL_SMS_BY_COUNTRY, params);
            boolean hadErrors = false;
            if (smsList.size() > 0) {
                List<Map<String,Object>> logInsertObjects = new ArrayList<>();
                Object[] delete = new Object[smsList.size()];
                int i = 0;
                for (Map<String, Object> map : smsList) {
                    try {
                        smsDispatcher.sendSmsToApi(map.get("SmsPhoneNumber").toString(),
                                map.get("Message").toString(),
                                _props);
                        delete[i] = map.get("SmsQueueId");
                        i++;
                        logInsertObjects.add(map);
                    } catch (Exception e) {
                        LOG.error("failed to send sms: " + map.get("Message") + " , from" +
                                " " + map.get("SmsPhoneNumber"), e);
                        hadErrors = true;
                    }
                }
                //delete from sms queue
                if(!Utils.isEmpty(delete)){
                    String sql = generateDeleteString(delete);
                    _dao.executeDelete(sql, null);
                }

                //store sms in log
                if(logInsertObjects.size()>0){
                    String sqlLog = generateInsertLogString(logInsertObjects);
                    _dao.executeUpdate(sqlLog, null);
                }
            }
            if (hadErrors) {
                response.message = "Some messages failed for " + smsDispatcher.getDispatcherName();
                response.status = 500;
            } else {
                response.message = "Sent " + smsDispatcher.getDispatcherName() + " SMSes";
                response.status = 200;
            }
        } catch (Exception e) {
            LOG.error("Error getting response", e);
            response.status = 500;
            response.message = e.getMessage();
        }

        return response;
    }
}
