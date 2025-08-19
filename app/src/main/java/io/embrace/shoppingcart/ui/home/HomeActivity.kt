package io.embrace.shoppingcart.ui.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.AndroidEntryPoint
import io.embrace.shoppingcart.presentation.home.HomeScreen
import io.embrace.shoppingcart.ui.theme.EmbraceShoppingCartTheme
import android.content.Intent
import io.embrace.shoppingcart.ui.cart.CartActivity
import io.embrace.shoppingcart.ui.profile.ProfileActivity

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmbraceShoppingCartTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.systemBars,
                    topBar = {
                        TopAppBar(
                            title = { Text("Home") },
                            actions = {
                                val ctx = LocalContext.current
                                IconButton(onClick = {
                                    ctx.startActivity(Intent(ctx, CartActivity::class.java))
                                }) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Open cart")
                                }
                                IconButton(onClick = {
                                    ctx.startActivity(Intent(ctx, ProfileActivity::class.java))
                                }) {
                                    Icon(Icons.Default.Person, contentDescription = "Open profile")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        HomeScreen()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeActivityPreview() {
    EmbraceShoppingCartTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.systemBars,
            topBar = {
                TopAppBar(
                    title = { Text("Home") },
                    actions = {
                        IconButton(onClick = { /* preview */ }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Open cart")
                        }
                        IconButton(onClick = { /* preview */ }) {
                            Icon(Icons.Default.Person, contentDescription = "Open profile")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                HomeScreen()
            }
        }
    }
}
