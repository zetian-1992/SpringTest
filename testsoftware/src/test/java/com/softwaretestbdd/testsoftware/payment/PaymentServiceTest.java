package com.softwaretestbdd.testsoftware.payment;

import com.softwaretestbdd.testsoftware.customer.Customer;
import com.softwaretestbdd.testsoftware.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService underTest;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CardPaymentCharger cardPaymentCharger;

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;

    @Test
    void isShouldChargeAndSavePayment() {
        //given CustomerId
        UUID customerId = UUID.randomUUID();
        //given Payment
        Payment payment = new Payment(null, null, new BigDecimal("100.00"),
                Currency.USD, "card1234", "donation");
        //given PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        given(cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        )).willReturn(new CardPaymentCharge(true));
        //when
        underTest.chargeCard(customerId, paymentRequest);
        //then
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

        assertThat(paymentArgumentCaptorValue).usingRecursiveComparison()
                .ignoringFields("customerId").isEqualTo(payment);

        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);

    }

    @Test
    void isShouldThrowWhenCustomerNotFound() {
        //given
        UUID customerId = UUID.randomUUID();

        given(customerRepository.findById(customerId)).willReturn(Optional.empty());
        //when
        //then

        assertThatThrownBy(() -> {
            underTest.chargeCard(customerId, mock(PaymentRequest.class));
        }).isInstanceOf(IllegalStateException.class).hasMessageContaining(String.format("Customer with id %s not found", customerId));

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void isShouldThrowWhenCurrencyNotSupported() {
        //given
        UUID customerId = UUID.randomUUID();
        //given Payment
        Payment payment = new Payment(null, null, new BigDecimal("100.00"),
                Currency.EUR, "card1234", "donation");
        //given PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        //when
        //then
        assertThatThrownBy(() -> {
            underTest.chargeCard(customerId, paymentRequest);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format(
                        "Currency [%s] is not supported",
                        paymentRequest.getPayment().getCurrency()
                ));
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void isShouldNotChargeWhenCardIsNotDebited() {
        //given
        UUID customerId = UUID.randomUUID();
        //given Payment
        Payment payment = new Payment(null, null, new BigDecimal("100.00"),
                Currency.USD, "card1234", "donation");
        //given PaymentRequest
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        given(cardPaymentCharger.chargeCard(
                payment.getSource(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getDescription()
        )).willReturn(new CardPaymentCharge(false));
        //when
        //then
        assertThatThrownBy(() -> {
            underTest.chargeCard(customerId, paymentRequest);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer %s", customerId));

        then(paymentRepository).shouldHaveNoInteractions();

    }
}