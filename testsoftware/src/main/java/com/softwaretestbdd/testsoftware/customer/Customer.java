package com.softwaretestbdd.testsoftware.customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@JsonIgnoreProperties(allowGetters = true)
public class Customer {

    @Id
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @NotBlank
    @Column(nullable = false)
    private String name;

    public Customer(UUID id, String name, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public Customer() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
