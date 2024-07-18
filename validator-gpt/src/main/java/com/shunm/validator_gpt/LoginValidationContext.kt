package com.shunm.validator_gpt

object LoginValidationContext {
    fun validate(username: String, email : String) : ValidationResult = ValidatorContext().validate {
        field(username) {
            notEmpty("Username is required")
            minLength(3, "Username must be at least 3 characters long")
        }
        field(email) {
            notEmpty("Email is required")
            email("Please enter a valid email address")
        }
    }
}