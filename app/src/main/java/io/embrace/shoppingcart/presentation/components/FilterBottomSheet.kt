package io.embrace.shoppingcart.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.embrace.shoppingcart.domain.model.Category
import io.embrace.shoppingcart.domain.model.ProductFilters
import io.embrace.shoppingcart.domain.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filters: ProductFilters,
    categories: List<Category>,
    onFiltersChanged: (ProductFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var localFilters by remember { mutableStateOf(filters) }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtros",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Clear, contentDescription = "Cerrar")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ordenamiento
            Text(
                text = "Ordenar por",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SortOption.values()) { sortOption ->
                    FilterChip(
                        selected = localFilters.sortOption == sortOption,
                        onClick = {
                            localFilters = localFilters.copy(sortOption = sortOption)
                        },
                        label = { Text(sortOption.displayName) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Categorías
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
                        selected = localFilters.selectedCategory == null,
                        onClick = {
                            localFilters = localFilters.copy(selectedCategory = null)
                        },
                        label = { Text("Todas") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = localFilters.selectedCategory == category.name,
                        onClick = {
                            localFilters = localFilters.copy(selectedCategory = category.name)
                        },
                        label = { Text(category.name) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Rango de precios
            Text(
                text = "Rango de precios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = localFilters.minPrice?.toString() ?: "",
                    onValueChange = { value ->
                        val minPrice = value.toIntOrNull()
                        localFilters = localFilters.copy(minPrice = minPrice)
                    },
                    label = { Text("Precio mínimo") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = localFilters.maxPrice?.toString() ?: "",
                    onValueChange = { value ->
                        val maxPrice = value.toIntOrNull()
                        localFilters = localFilters.copy(maxPrice = maxPrice)
                    },
                    label = { Text("Precio máximo") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Filtros adicionales
            Text(
                text = "Filtros adicionales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = localFilters.inStockOnly,
                    onCheckedChange = { checked ->
                        localFilters = localFilters.copy(inStockOnly = checked)
                    }
                )
                Text(
                    text = "Solo productos en stock",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Rating mínimo
            Text(
                text = "Rating mínimo: ${localFilters.minRating}",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = localFilters.minRating,
                onValueChange = { value ->
                    localFilters = localFilters.copy(minRating = value)
                },
                valueRange = 0f..5f,
                steps = 9
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        localFilters = ProductFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpiar filtros")
                }
                Button(
                    onClick = {
                        onFiltersChanged(localFilters)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aplicar filtros")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FilterButton(
    onClick: () -> Unit,
    hasActiveFilters: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Filtros")
        if (hasActiveFilters) {
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}
