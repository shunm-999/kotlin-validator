package com.shunm.validator

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Failure(val errors: List<ValidationError>) : ValidationResult {
        constructor(error : ValidationError) : this(listOf(error))
    }
}

fun ValidationResult.isSuccess() = this is ValidationResult.Success
fun ValidationResult.isFailure() = this is ValidationResult.Failure