package com.shunm.validator

data class ValidationRule<T>(
    val validation : (T) -> ValidationResult
)