package io.embrace.shoppingcart.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel(), onSuccess: (() -> Unit)? = null) {
    val state by viewModel.state.collectAsState()
    if (state.success) { onSuccess?.invoke() }

    Scaffold(topBar = { TopAppBar(title = { Text(if (state.mode == AuthViewModel.Mode.Login) "Login" else "Register") }) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.mode == AuthViewModel.Mode.Register) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::updateName,
                    label = { Text("Name") },
                    isError = state.nameError != null,
                    supportingText = { state.nameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            OutlinedTextField(
                value = state.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Email") },
                isError = state.emailError != null,
                supportingText = { state.emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("Password") },
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { viewModel.submit() }, enabled = !state.isLoading, modifier = Modifier.fillMaxWidth()) {
                Text(if (state.mode == AuthViewModel.Mode.Login) "Login" else "Create Account")
            }
            TextButton(onClick = { viewModel.switchMode() }) {
                Text(if (state.mode == AuthViewModel.Mode.Login) "No account? Register" else "Have an account? Login")
            }
            OutlinedButton(
                onClick = { viewModel.enterAsGuest() },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enter as guest")
            }
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            state.message?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}
