package com.softwaretestbdd.testsoftware.payment.stripe;

import com.softwaretestbdd.testsoftware.payment.CardPaymentCharge;
import com.softwaretestbdd.testsoftware.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    @InjectMocks
    private StripeService underTest;

    @Mock
    private StripeApi stripeApi;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

    @Captor
    private ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor;

    @Test
    void isShouldChargeCard() throws StripeException {
        //given

        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "donation";

        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(anyMap(), any())).willReturn(charge);
        //when
        CardPaymentCharge cardPaymentCharge = underTest.chargeCard(cardSource, amount, currency, description);
        //then

        then(stripeApi).should().create(mapArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());

        Map<String, Object> mapArgumentCaptorValue = mapArgumentCaptor.getValue();

        assertThat(mapArgumentCaptorValue).hasSize(4);

        assertThat(mapArgumentCaptorValue.get("amount")).isEqualTo(amount);
        assertThat(mapArgumentCaptorValue.get("currency")).isEqualTo(currency);
        assertThat(mapArgumentCaptorValue.get("source")).isEqualTo(cardSource);
        assertThat(mapArgumentCaptorValue.get("description")).isEqualTo(description);

        RequestOptions options = requestOptionsArgumentCaptor.getValue();

        assertThat(options).isNotNull();

        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }

    @Test
    void isShouldThrowWhenApiThrows() throws StripeException {
        //given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "donation";

        StripeException stripeException = mock(StripeException.class);
        given(stripeApi.create(anyMap(), any())).willThrow(stripeException);
        //when
        //then
        assertThatThrownBy(() -> {
            underTest.chargeCard(cardSource, amount, currency, description);
                }).isInstanceOf(IllegalStateException.class)
                .hasRootCause(stripeException)
                .hasMessageContaining("Cannot make stripe charge");

    }
}