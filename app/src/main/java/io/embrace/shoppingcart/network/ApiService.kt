package io.embrace.shoppingcart.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import io.embrace.shoppingcart.network.order.OrderRequest
import io.embrace.shoppingcart.network.order.OrderResponse

interface ApiService {
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @POST("orders")
    suspend fun placeOrder(@Body request: OrderRequest): OrderResponse
}

