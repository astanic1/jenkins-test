package factory;

import java.security.InvalidParameterException;

public class FactoryMaker {
    private static AbstractFactory pf=null;
    public static AbstractFactory getFactory(String factoryChoice) throws InvalidParameterException   {

        //TODO implement more factories if needed
        switch(factoryChoice){
            case "sms":
                return new SmsDispatcherFactory();
            default:
                throw new InvalidParameterException("Factory " + factoryChoice + " does not exist");
        }
    }
}
