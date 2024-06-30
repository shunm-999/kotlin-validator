package com.shunm.validator

import com.google.common.truth.Truth
import org.junit.Test

class ValidatorTest {

    @Test
    fun `string not empty`() {
        val validator = buildValidator<String> { value ->
            value.notEmpty("error")
        }
        Truth.assertThat(validator.validate("test")).isEqualTo(ValidationResult.Success)
    }

    @Test
    fun `string empty`() {
        val validator = buildValidator<String> { value ->
            value.notEmpty("error")
        }
        Truth.assertThat(validator.validate(""))
            .isEqualTo(
                ValidationResult.Failure(ValidationError("error")
                )
            )
    }
}