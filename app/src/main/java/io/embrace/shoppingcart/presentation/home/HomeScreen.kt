package io.embrace.shoppingcart.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.embrace.shoppingcart.presentation.components.ProductCard
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import io.embrace.shoppingcart.ui.product.ProductDetailActivity

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val pullRefreshState =
            rememberPullRefreshState(refreshing = state.isLoading, onRefresh = { viewModel.load() })

    val filtered = state.filteredProducts

    Box(modifier = Modifier.fillMaxSize().pullRefresh(pullRefreshState)) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                    value = state.filters.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    placeholder = { Text("Search products") }
            )

            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(state.categories) { category ->
                    Button(
                            onClick = { viewModel.onSearchQueryChange(category.name) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                    ) { Text(category.name) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(filtered) { product ->
                    ProductCard(
                        product = product,
                        onProductClick = {
                            val intent = ProductDetailActivity.createIntent(context, product.id)
                            context.startActivity(intent)
                        },
                        onAddToCartClick = { viewModel.addToCart(it) },
                        isAddingToCart = state.addingProductIds.contains(product.id),
                        modifier = Modifier.width(240.dp).padding(8.dp)
                    )
                }
            }
        }

        PullRefreshIndicator(
                refreshing = state.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
        )

        state.error?.let { msg ->
            Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(msg)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.load() }) { Text("Retry") }
            }
        }
    }
}
