package com.shunm.validator_gpt

class ValidatorContext {
    private val validators = mutableListOf<() -> ValidationResult>()

    fun validate(action: ValidatorContext.() -> Unit): ValidationResult {
        action()
        val results = validators.map { it() }
        val errors = results.filterIsInstance<ValidationResult.Failure>().flatMap { it.errors }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }


    fun field(value: Number, action: NumberFieldValidator.() -> Unit) {
        val validator = NumberFieldValidator(value)
        action(validator)
        validators.add {
            validator.validate()
        }
    }

    fun field(value: String, action: StringFieldValidator.() -> Unit) {
        val validator = StringFieldValidator(value)
        action(validator)
        validators.add {
            validator.validate()
        }
    }
}

private sealed interface FieldValidator {
    val rules: MutableList<() -> ValidationResult>

    fun validate(): ValidationResult {
        val results = rules.map { it() }
        val errors = results.filterIsInstance<ValidationResult.Failure>().flatMap { it.errors }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(errors)
        }
    }

    fun rule(errorMessage: String, predicate: () -> Boolean) {
        rules.add {
            if (predicate()) {
                ValidationResult.Success
            } else {
                ValidationResult.Failure(
                    ValidationError(errorMessage)
                )
            }
        }
    }
}


class NumberFieldValidator(private val value : Number) : FieldValidator {
    override val rules: MutableList<() -> ValidationResult> = mutableListOf()

    fun greaterThan(number: Number, errorMessage: String) {
        rule(errorMessage) {
            value.toDouble() > number.toDouble()
        }
    }

    fun lessThan(number: Number, errorMessage: String) {
        rule(errorMessage) {
            value.toDouble() < number.toDouble()
        }
    }

    fun equalTo(number: Number, errorMessage: String) {
        rule(errorMessage) {
            value.toDouble() == number.toDouble()
        }
    }
}

class StringFieldValidator(private val value: String) : FieldValidator {

    companion object {
        private val EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$".toRegex()
    }
    override val rules: MutableList<() -> ValidationResult> = mutableListOf()

    fun notEmpty(errorMessage: String) {
        rule(errorMessage) {
            value.isNotEmpty()
        }
    }

    fun minLength(minLength: Int, errorMessage: String) {
        rule(errorMessage) {
            value.length >= minLength
        }
    }

    fun maxLength(maxLength: Int, errorMessage: String) {
        rule(errorMessage) {
            value.length <= maxLength
        }
    }

    fun matches(regex: String, errorMessage: String) {
        rule(errorMessage) {
            value.matches(regex.toRegex())
        }
    }

    fun matches(regex: Regex, errorMessage: String) {
        rule(errorMessage) {
            value.matches(regex)
        }
    }

    fun email(errorMessage: String) {
        rule(errorMessage) {
            value.matches(EMAIL_REGEX)
        }
    }
}