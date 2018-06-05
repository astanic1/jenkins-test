package factory;

import com.mysql.jdbc.NotImplemented;
import sms.SmsDispatcher;
import sms.impl.BudgetSmsDispatcherImpl;
import sms.impl.GikkoSmsDispatherImpl;
import sms.impl.LmtGroupSmsDispatcherImpl;
import sms.impl.MTargetSmsDispatcherImpl;

import java.security.InvalidParameterException;

public class SmsDispatcherFactory extends AbstractFactory {
    @Override
    public SmsDispatcher getSmsDispatcher(String countryName) throws  NotImplemented {
        if(countryName==null){
            throw new InvalidParameterException("Country name cannot be null");
        }
        //TODO implement for other countries
        switch(countryName){
            case "Botswana":
                return new BudgetSmsDispatcherImpl();
            case "Cameroon":
                return new LmtGroupSmsDispatcherImpl();
            case "Rwanda":
                return new MTargetSmsDispatcherImpl();
            case "Zimbabwe":
                return new GikkoSmsDispatherImpl();
            default:
                throw new NotImplemented();
        }
    }
}
