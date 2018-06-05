package contract;

/**
 * Created by devlogic on 5/20/16.
 */
public class Payment {

    private String transactionid;
    private String description;
    private String reference_number;
    private String customer;
    private double amount;
    private String payment_method;

    public Payment() {
    }

    /**
     *
     * @param transactionid
     * @param description
     * @param reference_number
     * @param customer
     * @param amount
     * @param payment_method
     */
    public Payment(String transactionid, String description, String reference_number, String customer, double amount, String payment_method) {
        this.transactionid = transactionid;
        this.description = description;
        this.reference_number = reference_number;
        this.customer = customer;
        this.amount = amount;
        this.payment_method = payment_method;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;

        Payment payment = (Payment) o;

        if (Double.compare(payment.getAmount(), getAmount()) != 0) return false;
        if (getTransactionid() != null ? !getTransactionid().equals(payment.getTransactionid()) : payment.getTransactionid() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(payment.getDescription()) : payment.getDescription() != null)
            return false;
        if (getReference_number() != null ? !getReference_number().equals(payment.getReference_number()) : payment.getReference_number() != null)
            return false;
        if (getCustomer() != null ? !getCustomer().equals(payment.getCustomer()) : payment.getCustomer() != null)
            return false;
        return !(getPayment_method() != null ? !getPayment_method().equals(payment.getPayment_method()) : payment.getPayment_method() != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getTransactionid() != null ? getTransactionid().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getReference_number() != null ? getReference_number().hashCode() : 0);
        result = 31 * result + (getCustomer() != null ? getCustomer().hashCode() : 0);
        temp = Double.doubleToLongBits(getAmount());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getPayment_method() != null ? getPayment_method().hashCode() : 0);
        return result;
    }
}
