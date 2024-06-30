package com.shunm.kotlin_validator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.shunm.kotlin_validator.ui.component.items
import com.shunm.kotlin_validator.ui.theme.KotlinvalidatorTheme
import com.shunm.validator_gpt.ValidationResult
import com.shunm.validator_gpt.ValidatorContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinvalidatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        LoginForm(
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    modifier: Modifier = Modifier,
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var validationErrors by remember { mutableStateOf(listOf<String>()) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username") }
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") }
            )
            Button(onClick = {
                val validationContext = ValidatorContext()
                val result = validationContext.validate {
                    field(username) {
                        notEmpty("Username is required")
                        minLength(3, "Username must be at least 3 characters long")
                    }
                    field(email) {
                        notEmpty("Email is required")
                        email("Please enter a valid email address")
                    }
                }
                validationErrors = when (result) {
                    is ValidationResult.Success -> emptyList()
                    is ValidationResult.Failure -> result.errors.map { it.errorMessage }
                }
            }) {
                Text(text = "Submit")
            }
            items(validationErrors) { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginFormPreview() {
    KotlinvalidatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            LoginForm(
            )
        }
    }
}