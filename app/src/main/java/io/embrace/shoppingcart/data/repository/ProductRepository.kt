package io.embrace.shoppingcart.data.repository

import io.embrace.shoppingcart.domain.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
}


