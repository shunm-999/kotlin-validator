package com.shunm.validator_gpt

data class ValidationError(
    val errorMessage: String,
)

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Failure(val errors: List<ValidationError>) : ValidationResult {
        constructor(error : ValidationError) : this(listOf(error))
    }
}