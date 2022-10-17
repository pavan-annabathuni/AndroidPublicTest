package com.waycool.featurelogin.test;



import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidatorTest {
    @Test
    public void whenInputIsValid(){
        String number = "1234567899";
        boolean value = new  Validator(0).checkMobileNumber(number);
        assertThat(value).isEqualTo(true);
    }
    @Test
    public void whenInputIsInValid(){
        String number = "12345678";
        boolean value = new  Validator(0).checkMobileNumber(number);
        assertThat(value).isEqualTo(false);
    }
    @Test
    public void whenOTPtIsValid(){
        String number = "1234";
        boolean value = new  Validator(0).checkOTPNumber(number);
        assertThat(value).isEqualTo(true);
    }
    @Test
    public void whenOTPIsInValid(){
        String number = " ";
        boolean value = new  Validator(0).checkOTPNumber(number);
        assertThat(value).isEqualTo(false);
    }

}