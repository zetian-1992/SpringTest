package com.softwaretestbdd.testsoftware.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void isShouldSelectCustomerByPhoneNumber() {
        //given
        String phoneNumber = "1234567890";
        Customer customer = new Customer(UUID.randomUUID(), "James", phoneNumber);

        //when

        underTest.save(customer);
        //then
        Optional<Customer> customerOptional = underTest.findCustomerByPhoneNumber(phoneNumber);
        assertThat(customerOptional)
                .isPresent()
                .hasValueSatisfying( c -> {
                    assertThat(c).usingRecursiveComparison().isEqualTo(customer);
                });
    }

    @Test
    void isShouldNotSelectCustomerWhenPhoneNumberNotExist() {
        //given
        String phoneNumber = "23456";
        //when
        Optional<Customer> customerOptional = underTest.findCustomerByPhoneNumber(phoneNumber);
        //then
        assertThat(customerOptional).isNotPresent();
    }

    @Test
    void isShouldSaveCustomer() {
        //given
        String phoneNumber = "1234567890";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "James", phoneNumber);
        //when
        underTest.save(customer);
        //then
        Optional<Customer> customer1 = underTest.findById(id);

        assertThat(customer1)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).usingRecursiveComparison()
                            .isEqualTo(customer);
                });
    }

    @Test
    void isShouldNotSaveCustomerWhenNameIsNull() {
        //given
        String phoneNumber = "1234567890";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, phoneNumber);
        //when
        //then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.softwaretestbdd.testsoftware.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void isShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        //given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "James", null);
        //when
        //then
        assertThatThrownBy(() -> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.softwaretestbdd.testsoftware.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}