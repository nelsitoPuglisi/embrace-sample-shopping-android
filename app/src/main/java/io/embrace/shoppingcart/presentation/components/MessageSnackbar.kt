package io.embrace.shoppingcart.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MessageSnackbar(message: String, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Snackbar(
            modifier = modifier.padding(16.dp),
            action = { TextButton(onClick = onDismiss) { Text("OK") } }
    ) { Text(message) }
}
