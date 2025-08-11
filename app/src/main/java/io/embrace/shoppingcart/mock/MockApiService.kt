package io.embrace.shoppingcart.mock

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.embrace.shoppingcart.network.ApiService
import io.embrace.shoppingcart.network.ProductDto
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject

class MockApiService @Inject constructor(
    private val context: Context,
    private val moshi: Moshi,
    private val config: MockNetworkConfig
) : ApiService {

    private val adapter = moshi.adapter<List<ProductDto>>(
        Types.newParameterizedType(List::class.java, ProductDto::class.java)
    )

    override suspend fun getProducts(): List<ProductDto> {
        return when (config.scenario) {
            NetworkScenario.SUCCESS -> {
                delay(config.defaultDelayMs)
                loadProducts()
            }
            NetworkScenario.FAILURE -> {
                delay(config.defaultDelayMs)
                throw IOException("Simulated network failure")
            }
            NetworkScenario.SLOW -> {
                delay(config.slowDelayMs)
                loadProducts()
            }
        }
    }

    private fun loadProducts(): List<ProductDto> {
        val json = context.assets.open("products.json").bufferedReader().use { it.readText() }
        return adapter.fromJson(json) ?: emptyList()
    }
}
