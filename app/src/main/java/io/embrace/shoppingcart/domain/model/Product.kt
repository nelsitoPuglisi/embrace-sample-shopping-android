package io.embrace.shoppingcart.domain.model

data class Product(
    val id: String,
    val name: String,
    val priceCents: Int,
    val description: String,
    val imageUrl: String,
    val category: String = "",
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val inStock: Boolean = true,
    val discountPercentage: Int = 0
)


