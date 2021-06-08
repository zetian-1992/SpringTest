package com.softwaretestbdd.testsoftware.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwaretestbdd.testsoftware.customer.Customer;
import com.softwaretestbdd.testsoftware.customer.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void isShouldCreatePaymentSuccessfully() throws Exception {
        //given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "1234567");

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);
        ResultActions resultActions = mockMvc.perform(put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));

        long paymentId = 1L;
        Payment payment = new Payment(paymentId, customerId, new BigDecimal("100.00"), Currency.USD, "card1234", "donation");
        PaymentRequest paymentRequest = new PaymentRequest(payment);
        //when
        ResultActions paymentResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));
        //then
        paymentResultActions.andExpect(status().isOk());
        assertThat(paymentRepository.findById(paymentId)).isPresent()
                .hasValueSatisfying(p -> {
                    assertThat(p).usingRecursiveComparison()
                            .isEqualTo(payment);
                });
        //when
        //then
        resultActions.andExpect(status().isOk());
    }



    private String objectToJson(Object customer) {
        try {
            return new ObjectMapper().writeValueAsString(customer);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }

    }
}
