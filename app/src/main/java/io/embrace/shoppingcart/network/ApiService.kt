package io.embrace.shoppingcart.network

import retrofit2.http.GET

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<String>
}


