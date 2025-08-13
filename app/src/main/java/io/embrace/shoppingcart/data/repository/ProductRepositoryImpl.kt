package io.embrace.shoppingcart.data.repository

import io.embrace.shoppingcart.data.local.ProductDao
import io.embrace.shoppingcart.data.local.ProductEntity
import io.embrace.shoppingcart.domain.model.Product
import io.embrace.shoppingcart.mock.MockNetworkConfig
import io.embrace.shoppingcart.network.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao: ProductDao,
    private val networkConfig: MockNetworkConfig
) : ProductRepository {
    override suspend fun getProducts(): List<Product> {
        delay(networkConfig.defaultDelayMs)
        val products = apiService.getProducts()
        val entities = products.map { dto ->
            ProductEntity(
                id = dto.id,
                name = dto.name,
                priceCents = dto.priceCents,
                description = dto.description,
                imageUrl = dto.imageUrl,
                category = dto.category,
                rating = dto.rating,
                reviewCount = dto.reviewCount,
                inStock = dto.inStock,
                discountPercentage = dto.discountPercentage
            )
        }
        productDao.upsertAll(entities)
        return entities.map { entity ->
            Product(
                id = entity.id,
                name = entity.name,
                priceCents = entity.priceCents,
                description = entity.description,
                imageUrl = entity.imageUrl,
                category = entity.category,
                rating = entity.rating,
                reviewCount = entity.reviewCount,
                inStock = entity.inStock,
                discountPercentage = entity.discountPercentage
            )
        }
    }

    override fun observeProducts() =
        productDao.observeAll().map { list ->
            list.map { entity ->
                Product(
                    id = entity.id,
                    name = entity.name,
                    priceCents = entity.priceCents,
                    description = entity.description,
                    imageUrl = entity.imageUrl,
                    category = entity.category,
                    rating = entity.rating,
                    reviewCount = entity.reviewCount,
                    inStock = entity.inStock,
                    discountPercentage = entity.discountPercentage
                )
            }
        }
}


