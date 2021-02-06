package pl.foxsaysderp.voucherstore.sales.payment;

import pl.foxsaysderp.payu.exceptions.PayUException;

public class PaymentException extends IllegalStateException {
    public PaymentException(PayUException e) {
        super(e);
    }
}
