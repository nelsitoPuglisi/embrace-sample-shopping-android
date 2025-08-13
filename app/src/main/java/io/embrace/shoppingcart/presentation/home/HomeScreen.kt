package io.embrace.shoppingcart.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.domain.model.ProductFilters
import io.embrace.shoppingcart.presentation.components.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val gridState = rememberLazyGridState()
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    
    // Detectar cuando el usuario llega al final de la lista para cargar más
    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                if (lastVisibleItem != null && 
                    lastVisibleItem.index >= state.filteredProducts.size - 3 &&
                    !state.isLoadingMore && 
                    !state.hasReachedEnd) {
                    viewModel.loadMore()
                }
            }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when {
            state.isLoading -> {
                LoadingScreen()
            }
            state.error != null -> {
                ErrorScreen(
                    message = state.error!!,
                    onRetry = { viewModel.load() }
                )
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header con búsqueda y filtros
                    ProductListHeader(
                        searchQuery = state.filters.searchQuery,
                        onSearchQueryChange = viewModel::onSearchQueryChange,
                        onFilterClick = { showFilterBottomSheet = true },
                        hasActiveFilters = state.filters != ProductFilters(),
                        categories = state.categories,
                        onCategorySelected = viewModel::onCategorySelected,
                        selectedCategory = state.filters.selectedCategory
                    )
                    
                    // Lista de productos
                    if (state.filteredProducts.isEmpty() && !state.isLoading) {
                        EmptyState(
                            message = if (state.filters.searchQuery.isNotEmpty()) {
                                "No se encontraron productos para '${state.filters.searchQuery}'"
                            } else {
                                "No hay productos disponibles"
                            }
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 160.dp),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = gridState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.filteredProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = onProductClick,
                                    onFavoriteClick = { /* TODO: Implementar favoritos */ },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            // Indicador de carga de más productos
                            if (state.isLoadingMore) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    LoadingMoreIndicator()
                                }
                            }
                            
                            // Indicador de fin de lista
                            if (state.hasReachedEnd && state.filteredProducts.isNotEmpty()) {
                                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                                    EndOfListIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pull to refresh indicator
        PullRefreshIndicator(
            refreshing = state.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
    
    // Bottom sheet de filtros
    if (showFilterBottomSheet) {
        FilterBottomSheet(
            filters = state.filters,
            categories = state.categories,
            onFiltersChanged = { newFilters ->
                // Aplicar los nuevos filtros
                viewModel.onCategorySelected(newFilters.selectedCategory)
                viewModel.onSortOptionSelected(newFilters.sortOption)
                viewModel.onPriceRangeChanged(newFilters.minPrice, newFilters.maxPrice)
                viewModel.onInStockOnlyChanged(newFilters.inStockOnly)
                viewModel.onMinRatingChanged(newFilters.minRating)
            },
            onDismiss = { showFilterBottomSheet = false }
        )
    }
}

@Composable
fun ProductListHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean,
    categories: List<io.embrace.shoppingcart.domain.model.Category>,
    onCategorySelected: (String?) -> Unit,
    selectedCategory: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Barra de búsqueda y filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Buscar productos...") },
                singleLine = true
            )
            FilterButton(
                onClick = onFilterClick,
                hasActiveFilters = hasActiveFilters
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Categorías
        if (categories.isNotEmpty()) {
            Text(
                text = "Categorías",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelected(null) },
                        label = { Text("Todas") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category.name,
                        onClick = { onCategorySelected(category.name) },
                        label = { Text(category.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
