package io.embrace.shoppingcart.presentation.main

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.embrace.shoppingcart.ui.checkout.CheckoutActivity
import io.embrace.shoppingcart.ui.home.HomeActivity
import io.embrace.shoppingcart.ui.product.ProductDetailActivity
import io.embrace.shoppingcart.ui.profile.ProfileActivity

@Composable
fun MainActionsScreen() {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }) { Text("Go to Home") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            context.startActivity(Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("productId", "123")
            })
        }) { Text("Go to Product Detail (id=123)") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            context.startActivity(Intent(context, CheckoutActivity::class.java))
        }) { Text("Go to Checkout") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            context.startActivity(Intent(context, ProfileActivity::class.java))
        }) { Text("Go to Profile") }
    }
}


