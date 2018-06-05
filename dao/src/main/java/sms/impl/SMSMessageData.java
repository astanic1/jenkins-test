package sms.impl;

import java.util.List;

/**
 * Created by devlogic on 1/10/17.
 */
public class SMSMessageData {

    private List<Recipients> Recipients;

    public List<sms.impl.Recipients> getRecipients() {
        return Recipients;
    }

    public void setRecipients(List<sms.impl.Recipients> recipients) {
        Recipients = recipients;
    }
}
