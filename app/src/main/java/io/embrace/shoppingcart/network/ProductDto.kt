package io.embrace.shoppingcart.network

data class ProductDto(
    val id: String,
    val name: String,
    val priceCents: Int,
    val description: String,
    val imageUrl: String
)
