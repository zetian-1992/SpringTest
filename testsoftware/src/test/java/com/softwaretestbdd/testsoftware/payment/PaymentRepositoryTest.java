package com.softwaretestbdd.testsoftware.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository underTest;

    @BeforeEach
    void setUp() {

    }

    @Test
    void isShouldSavePayment() {
        //given
        long paymentId = 1L;
        Payment payment = new Payment(paymentId, UUID.randomUUID(), new BigDecimal("100.00"), Currency.USD, "masterCard", "donation");

        //when
        underTest.save(payment);
        //then

        Optional<Payment> paymentOptional = underTest.findById(paymentId);
        assertThat(paymentOptional).isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p).usingRecursiveComparison()
                            .isEqualTo(payment);
                });
    }
}