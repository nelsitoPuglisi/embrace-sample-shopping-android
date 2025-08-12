package io.embrace.shoppingcart.ui.product

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import io.embrace.shoppingcart.ui.theme.EmbraceShoppingCartTheme

@AndroidEntryPoint
class ProductDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productId = intent.getStringExtra("productId")
            ?: intent?.data?.getQueryParameter("id")
        enableEdgeToEdge()
        setContent {
            EmbraceShoppingCartTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.systemBars
                ) { innerPadding ->
                    Text(
                        text = "Product detail: ${'$'}productId",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailActivityPreview() {
    EmbraceShoppingCartTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->
            Text(
                text = "Product detail: 123",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}


