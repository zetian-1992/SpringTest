package com.softwaretestbdd.testsoftware.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationServiceTest {

    private CustomerRegistrationService underTest;

    @Mock
    private CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void isShouldSaveNewCustomer() {
        //given
        String phoneNumber = "1234567890";
        Customer customer = new Customer(UUID.randomUUID(), "James", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //when
        underTest.registerNewCustomer(request);
        //then

        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).usingRecursiveComparison().isEqualTo(customer);
    }

    @Test
    void isShouldSaveNewCustomerWhenIdIsNull() {
        //given
        String phoneNumber = "1234567890";
        Customer customer = new Customer(null, "James", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
        //when
        underTest.registerNewCustomer(request);
        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customer1 = customerArgumentCaptor.getValue();
        assertThat(customer1).usingRecursiveComparison()
                .ignoringFields("id").isEqualTo(customer);

        assertThat(customer1.getId()).isNotNull();
    }

    @Test
    void isShouldNotSaveWhenCustomerExists() {
        //given
        String phoneNumber = "1234567890";
        Customer customer = new Customer(UUID.randomUUID(), "James", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));
        //when

        underTest.registerNewCustomer(request);
        //then

        then(customerRepository).should(never()).save(any());
    }

    @Test
    void isShouldThrowWhenPhoneNumberIsTaken() {
        //given
        String phoneNumber = "1234567890";
        Customer customer = new Customer(UUID.randomUUID(), "James", phoneNumber);
        Customer customer2 = new Customer(UUID.randomUUID(), "Kris", phoneNumber);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        given(customerRepository.findCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer2));
        //when
        //then

        assertThatThrownBy(() -> {
            underTest.registerNewCustomer(request);
        }).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        then(customerRepository).should(never()).save(any());
    }
}