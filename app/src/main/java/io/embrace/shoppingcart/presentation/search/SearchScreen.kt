package io.embrace.shoppingcart.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.embrace.shoppingcart.presentation.components.ProductCard
import io.embrace.shoppingcart.presentation.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: SearchViewModel = hiltViewModel()) {
    val products by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Catálogo de Productos") },
                        actions = {
                            IconButton(onClick = { viewModel.load() }) {
                                Icon(Icons.Default.Search, contentDescription = "Buscar")
                            }
                        }
                )
            }
    ) { paddingValues ->
        LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(paddingValues)
        ) {
            items(products) { product ->
                ProductCard(
                        product = product,
                        onProductClick = {
                            navController.navigate(
                                    Routes.ProductDetail.replace("{productId}", product.id)
                            )
                        },
                        onFavoriteClick = { /* TODO: Implementar favoritos */},
                        modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
