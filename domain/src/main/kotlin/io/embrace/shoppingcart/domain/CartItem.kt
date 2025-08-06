package io.embrace.shoppingcart.domain

/**
 * A quantity of a particular product in the shopping cart.
 */
data class CartItem(
    val product: Product,
    val quantity: Int,
)
