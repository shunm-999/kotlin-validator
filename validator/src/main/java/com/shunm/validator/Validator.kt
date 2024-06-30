package com.shunm.validator

interface Validatable<T> {
    fun validate(value: T): ValidationResult
}

private data class Validator<T>(
    private val validation: (T) -> ValidationResult,
) : Validatable<T> {
    override fun validate(value: T): ValidationResult {
        return validation(value)
    }
}

private data class CombinedValidator<T>(
    private val inners: List<Validatable<T>>,
) : Validatable<T> {

    override fun validate(value: T): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        for (inner in inners) {
            when (val result = inner.validate(value)) {
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

private data class LazyValidator<T>(
    val init: (T) -> Validatable<T>,
) : Validatable<T> {
    override fun validate(value: T): ValidationResult {
        val validator = init(value)
        return validator.validate(value)
    }
}


class ValidatorBuilder<T> {

    private val validators = mutableListOf<Validatable<Unit>>()

    fun String.notEmpty(errorMessage: String) {
        validators += validator(errorMessage) { this.isNotEmpty() }
    }

    fun String.minLength(length: Int, errorMessage: String) {
        validators += validator(errorMessage) { this.length >= length }
    }

    fun String.maxLength(length: Int, errorMessage: String) {
        validators += validator(errorMessage) { this.length <= length }
    }

    fun String.matches(regex: Regex, errorMessage: String) {
        validators += validator(errorMessage) { this.matches(regex) }
    }

    fun String.matches(regex: String, errorMessage: String) {
        validators += validator(errorMessage) { this.matches(regex.toRegex()) }
    }

    fun String.email(errorMessage: String) {
        validators += validator(errorMessage) { this.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$")) }
    }

    fun Int.inRange(min: Int, max: Int, errorMessage: String) {
        validators += validator(errorMessage) { this in min..max }
    }

    fun build(): Validatable<T> {
        val validator = CombinedValidator(validators)
        return validator {
            validator.validate(Unit)
        }
    }
}

fun <T> validator(validation: (T) -> ValidationResult): Validatable<T> {
    return Validator(validation)
}

fun <T> validator(errorMessage: String, validation: (T) -> Boolean): Validatable<T> {
    return Validator { value ->
        if (validation(value)) {
            ValidationResult.Success
        } else {
            ValidationResult.Failure(
                error = ValidationError(errorMessage)
            )
        }
    }
}

fun <T> buildValidator(builderAction: ValidatorBuilder<T>.(T) -> Unit): Validatable<T> {
    return LazyValidator { value ->
        val builder = ValidatorBuilder<T>()
        builder.builderAction(value)
        builder.build()
    }
}