package io.embrace.shoppingcart.mock

enum class NetworkScenario { SUCCESS, FAILURE, SLOW }

data class MockNetworkConfig(
    val defaultDelayMs: Long = 0L,
    val slowDelayMs: Long = 3000L,
    val scenario: NetworkScenario = NetworkScenario.SUCCESS
)
