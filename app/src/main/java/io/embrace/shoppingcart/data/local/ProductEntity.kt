package io.embrace.shoppingcart.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val priceCents: Int,
    val description: String,
    val imageUrl: String
)


