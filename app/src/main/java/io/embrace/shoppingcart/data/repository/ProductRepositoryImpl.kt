package io.embrace.shoppingcart.data.repository

import io.embrace.shoppingcart.data.local.ProductDao
import io.embrace.shoppingcart.data.local.ProductEntity
import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.network.ApiService
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao
) : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        val names = apiService.getProducts()
        val entities = names.mapIndexed { index, name ->
            ProductEntity(id = index.toString(), name = name, priceCents = (index + 1) * 1000)
        }
        productDao.upsertAll(entities)
        return entities.map { Product(it.id, it.name, it.priceCents) }
    }

    override fun observeProducts() =
        productDao.observeAll().map { list -> list.map { Product(it.id, it.name, it.priceCents) } }
}


