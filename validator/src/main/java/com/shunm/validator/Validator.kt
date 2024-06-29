package com.shunm.validator

data class Validator<T>(
    private val rules: List<ValidationRule<T>>,
) {

    fun validate(value: T): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        for (rule in rules) {
            when (val result = rule.validation(value)) {
                ValidationResult.Success -> {
                }

                is ValidationResult.Failure -> {
                    errors += result.errors
                }
            }
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(
                errors = errors
            )
        }
    }
}

class ValidatorBuilder<T> {
    private val rules = mutableListOf<ValidationRule<T>>()

    fun build(): Validator<T> {
        return Validator(
            rules = rules
        )
    }
}

fun <T> validator(builderAction: ValidatorBuilder<T>.() -> Unit): Validator<T> {
    val builder = ValidatorBuilder<T>()
    builder.builderAction()
    return builder.build()
}