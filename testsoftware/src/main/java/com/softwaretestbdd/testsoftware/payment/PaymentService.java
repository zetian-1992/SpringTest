package com.softwaretestbdd.testsoftware.payment;

import com.softwaretestbdd.testsoftware.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, CustomerRepository customerRepository, CardPaymentCharger cardPaymentCharger) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.cardPaymentCharger = cardPaymentCharger;
    }

    public void chargeCard (UUID customerId, PaymentRequest paymentRequest) {

        boolean isCustomerFound = customerRepository.findById(customerId).isPresent();
        if(!isCustomerFound) {
            throw new IllegalStateException(String.format("Customer with id %s not found", customerId));
        }

        boolean isCurrencySupported = ACCEPTED_CURRENCIES.contains(paymentRequest.getPayment().getCurrency());

        if(!isCurrencySupported) {
            String message = String.format(
                    "Currency [%s] is not supported",
                    paymentRequest.getPayment().getCurrency()
            );
            throw new IllegalStateException(message);
        }

       CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        );

        if (!cardPaymentCharge.isCardDebited()) {
            throw new IllegalStateException(String.format("Card not debited for customer %s", customerId));
        }

        paymentRequest.getPayment().setCustomerId(customerId);

        paymentRepository.save(paymentRequest.getPayment());
    }

}
