package io.embrace.shoppingcart.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.domain.usecase.GetProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<List<Product>>(emptyList())
    val state: StateFlow<List<Product>> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = getProducts()
        }
    }
}


