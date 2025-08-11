package io.embrace.shoppingcart.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.domain.usecase.GetProductsUseCase
import io.embrace.shoppingcart.domain.usecase.ObserveProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    observeProducts: ObserveProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<List<Product>>(emptyList())
    val state: StateFlow<List<Product>> = _state
    val liveData = state.asLiveData()

    init {
        viewModelScope.launch {
            observeProducts().collectLatest { _state.value = it }
        }
    }

    fun load() {
        viewModelScope.launch { getProducts() }
    }
}


