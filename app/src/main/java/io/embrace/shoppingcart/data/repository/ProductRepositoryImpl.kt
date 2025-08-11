package io.embrace.shoppingcart.data.repository

import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.network.ApiService
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductRepository {
    override suspend fun getProducts(): List<Product> =
        apiService.getProducts().mapIndexed { index, name ->
            Product(id = index.toString(), name = name, priceCents = (index + 1) * 1000)
        }
}


