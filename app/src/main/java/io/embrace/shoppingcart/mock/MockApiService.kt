package io.embrace.shoppingcart.mock

import io.embrace.shoppingcart.network.ApiService

class MockApiService : ApiService {
    override suspend fun getProducts(): List<String> = listOf(
        "Camiseta",
        "Pantalón",
        "Zapatillas",
        "Gorra"
    )
}


