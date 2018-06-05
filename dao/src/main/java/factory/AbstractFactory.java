package factory;

import com.mysql.jdbc.NotImplemented;
import sms.SmsDispatcher;

public abstract class AbstractFactory {
    public abstract SmsDispatcher getSmsDispatcher(String countryName) throws  NotImplemented;
}
