package io.embrace.shoppingcart.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.domain.model.ProductVariant
import io.embrace.shoppingcart.domain.usecase.GetProductsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailViewModel
@Inject
constructor(private val getProductsUseCase: GetProductsUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val products = getProductsUseCase()
                val product = products.find { it.id == productId }
                if (product != null) {
                    _uiState.value = _uiState.value.copy(product = product, isLoading = false)
                } else {
                    _uiState.value =
                            _uiState.value.copy(error = "Producto no encontrado", isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.value =
                        _uiState.value.copy(
                                error = "Error al cargar el producto: ${e.message}",
                                isLoading = false
                        )
            }
        }
    }

    fun selectSize(size: String) {
        _uiState.value =
                _uiState.value.copy(
                        selectedVariant = _uiState.value.selectedVariant.copy(size = size)
                )
    }

    fun selectColor(color: String) {
        _uiState.value =
                _uiState.value.copy(
                        selectedVariant = _uiState.value.selectedVariant.copy(color = color)
                )
    }

    fun updateQuantity(quantity: Int) {
        if (quantity > 0) {
            _uiState.value = _uiState.value.copy(quantity = quantity)
        }
    }

    fun addToCart() {
        val product = _uiState.value.product ?: return
        val quantity = _uiState.value.quantity

        // Aquí normalmente agregarías el producto al carrito
        // Por ahora solo actualizamos el estado
        _uiState.value =
                _uiState.value.copy(cartMessage = "Agregado al carrito: ${product.name} x$quantity")
    }

    fun clearCartMessage() {
        _uiState.value = _uiState.value.copy(cartMessage = null)
    }

    fun shareProduct() {
        val product = _uiState.value.product ?: return
        // Aquí implementarías la funcionalidad de compartir
        _uiState.value = _uiState.value.copy(shareMessage = "¡Mira este producto: ${product.name}!")
    }

    fun clearShareMessage() {
        _uiState.value = _uiState.value.copy(shareMessage = null)
    }
}

data class ProductDetailUiState(
        val product: Product? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedVariant: ProductVariant = ProductVariant(),
        val quantity: Int = 1,
        val cartMessage: String? = null,
        val shareMessage: String? = null
)
