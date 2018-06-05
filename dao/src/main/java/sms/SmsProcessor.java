package sms;

import contract.Response;

public interface SmsProcessor {

     Response processSms(String countryName);
}
