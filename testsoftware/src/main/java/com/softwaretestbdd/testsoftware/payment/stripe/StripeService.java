package com.softwaretestbdd.testsoftware.payment.stripe;

import com.softwaretestbdd.testsoftware.payment.CardPaymentCharge;
import com.softwaretestbdd.testsoftware.payment.CardPaymentCharger;
import com.softwaretestbdd.testsoftware.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "true"
)
public class StripeService implements CardPaymentCharger {

    private final StripeApi stripeApi;

    private final static RequestOptions requestOptions = RequestOptions.builder()
            .setApiKey("sk_test_4eC39HqLyjWDarjtT1zdp7dc")
            .build();
    @Autowired
    public StripeService(StripeApi stripeApi) {
        this.stripeApi = stripeApi;
    }

    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {

        Map<String, Object> params = new HashMap<>(){{
            put("amount", amount);
            put("currency", currency);
            put("source", cardSource);
            put("description", description);
        }};

        try {
            Charge charge = stripeApi.create(params, requestOptions);
            return new CardPaymentCharge(charge.getPaid());
        } catch (StripeException e) {
            throw new IllegalStateException("Cannot make stripe charge", e);
        }
    }
}
