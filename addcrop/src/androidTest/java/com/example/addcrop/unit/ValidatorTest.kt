package com.example.addcrop.unit

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidatorTest{
    @Test
    fun whenInputValid(){
        val name="praveen"
        val area=50
        val result=Validator.validateInput(name,area)
        assertThat(result).isEqualTo(true)
    }

    @Test
    fun whenInputIsInvalid(){
        val name = ""
        val area = 0
        val result = Validator.validateInput(name,area)
        assertThat(result).isEqualTo(false)
    }
}