package io.embrace.shoppingcart.domain.model

enum class SortOption(val displayName: String) {
    NAME_ASC("Nombre A-Z"),
    NAME_DESC("Nombre Z-A"),
    PRICE_ASC("Precio: Menor a Mayor"),
    PRICE_DESC("Precio: Mayor a Menor"),
    RATING_DESC("Mejor Valorados"),
    NEWEST("Más Recientes"),
    POPULAR("Más Populares")
}

