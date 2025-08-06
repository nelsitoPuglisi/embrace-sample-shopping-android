package io.embrace.shoppingcart.domain

/**
 * Represents an item available for purchase.
 */
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
)
