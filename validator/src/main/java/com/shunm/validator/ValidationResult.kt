package com.shunm.validator

sealed interface ValidationResult {
    data object Success : ValidationResult
    data class Failure(val errors: List<ValidationError>) : ValidationResult
}