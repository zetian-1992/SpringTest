package com.softwaretestbdd.testsoftware.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PhoneNumberValidatorTest {

    @InjectMocks
    private PhoneNumberValidator underTest;

    @ParameterizedTest
    @CsvSource({"+4471234567, true", "4354346, false"})
    void isShouldValidatePhoneNumber(String input, String expected) {
        //given
        String phoneNumber = "+4471234567";

        //when
        boolean isValidate = underTest.test(input);
        //then

        assertThat(isValidate).isEqualTo(Boolean.valueOf(expected));
    }

    @Test
    void isShouldValidatePhoneNumberWhenIncorrectHasLengthLonger() {
        //given
        String phoneNumber = "+44712345670";

        //when
        boolean isValidate = underTest.test(phoneNumber);
        //then

        assertThat(isValidate).isFalse();
    }
}
