package sms.impl;

import sms.SmsDispatcher;
import org.smpp.Data;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;

import java.util.Properties;

/**
 * Created by devlogic on 11/9/16.
 */
public class EmpawaSmsDispatcherImpl implements SmsDispatcher {

    public void sendSmsToApi(String receiver, String message, Properties properties) throws Exception {
        try {
            SubmitSM request = new SubmitSM();
            request.setSourceAddr(createAddress(properties.getProperty("empawa.source.address")));
            request.setDestAddr(createAddress(receiver));
            request.setShortMessage(message);
            // request.setScheduleDeliveryTime(deliveryTime);
            request.setReplaceIfPresentFlag((byte) 0);
            request.setEsmClass((byte) 0);
            request.setProtocolId((byte) 0);
            request.setPriorityFlag((byte) 0);
            request.setRegisteredDelivery((byte) 1); // we want delivery reports
            request.setDataCoding((byte) 0);
            request.setSmDefaultMsgId((byte) 0);

            Session session = getSession(properties.getProperty("empawa.host"),
                    Integer.parseInt(properties.getProperty("empawa.port")),
                    properties.getProperty("empawa.username"),
                    properties.getProperty("empawa.password"));
            SubmitSMResp response = session.submit(request);
            System.out.println(response);
        } catch (Throwable e) {
            throw e;
        }

    }

    @Override
    public String getDispatcherName() {
        return null;
    }

    private Session getSession(String smscHost, int smscPort, String smscUsername, String smscPassword) throws Exception{
      /*  if(sessionMap.containsKey(smscUsername)) {
            return sessionMap.get(smscUsername);
        }*/

        BindRequest request = new BindTransmitter();
        request.setSystemId(smscUsername);
        request.setPassword(smscPassword);
        // request.setSystemType(systemType);
        // request.setAddressRange(addressRange);
        request.setInterfaceVersion((byte) 0x34); // SMPP protocol version

        TCPIPConnection connection = new TCPIPConnection(smscHost, smscPort);
        // connection.setReceiveTimeout(BIND_TIMEOUT);
        Session session = new Session(connection);
        //   sessionMap.put(smscUsername, session);

        BindResponse response = session.bind(request);
        return session;
    }

    private Address createAddress(String address) throws WrongLengthOfStringException {
        Address addressInst = new Address();
        addressInst.setTon((byte) 5); // national ton
        addressInst.setNpi((byte) 0); // numeric plan indicator
        addressInst.setAddress(address, Data.SM_ADDR_LEN);
        return addressInst;
    }
}
