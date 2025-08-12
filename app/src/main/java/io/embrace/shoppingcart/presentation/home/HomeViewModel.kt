package io.embrace.shoppingcart.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embrace.shoppingcart.domain.model.Category
import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.domain.usecase.GetCategoriesUseCase
import io.embrace.shoppingcart.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val getCategories: GetCategoriesUseCase
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val products: List<Product> = emptyList(),
        val categories: List<Category> = emptyList(),
        val searchQuery: String = "",
        val error: String? = null
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val productsDeferred = async { getProducts() }
                val categoriesDeferred = async { getCategories() }
                val products = productsDeferred.await()
                val categories = categoriesDeferred.await()
                _state.value = _state.value.copy(
                    products = products,
                    categories = categories,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }
}
