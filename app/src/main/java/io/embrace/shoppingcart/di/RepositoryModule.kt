package io.embrace.shoppingcart.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.embrace.shoppingcart.data.repository.ProductRepository
import io.embrace.shoppingcart.data.repository.ProductRepositoryImpl
import io.embrace.shoppingcart.data.repository.UserRepository
import io.embrace.shoppingcart.data.repository.UserRepositoryImpl
import io.embrace.shoppingcart.data.repository.CategoryRepository
import io.embrace.shoppingcart.data.repository.CategoryRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository
}


