package io.embrace.shoppingcart.domain

/**
 * Overview of the shopping cart's contents and total price.
 */
data class CartSummary(
    val items: List<CartItem>,
    val totalPrice: Double,
)
