package contract;

/**
 * Created by Kristina on 9/29/17.
 */
public class PNGSmsRequest {

    private String username;
    private String password;
    private String[] MSISDN;
    private String message;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getMSISDN() {
        return MSISDN;
    }

    public void setMSISDN(String[] MSISDN) {
        this.MSISDN = MSISDN;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
