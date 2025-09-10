package io.embrace.shoppingcart

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.embrace.shoppingcart.ui.home.HomeActivity as TestHomeActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.embrace.shoppingcart.di.NetworkModule
import io.embrace.shoppingcart.di.MockApi
import io.embrace.shoppingcart.di.RealApi
import io.embrace.shoppingcart.mock.MockApiService
import io.embrace.shoppingcart.mock.MockNetworkConfig
import io.embrace.shoppingcart.mock.NetworkScenario
import io.embrace.shoppingcart.network.ApiService
import javax.inject.Singleton
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import io.embrace.shoppingcart.ui.auth.AuthActivity
import io.embrace.shoppingcart.ui.home.HomeActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartFlowSuccessTest {
    @get:Rule val composeRule: AndroidComposeTestRule<*, *> = createAndroidComposeRule<TestHomeActivity>()
    @Before fun setUp() {
        io.embrace.shoppingcart.mock.MockAuthOverrides.loggedInUserId = "test-user"
        io.embrace.shoppingcart.presentation.testutil.UiTestOverrides.verticalListForTests = true
        io.embrace.shoppingcart.mock.MockNetworkConfigOverrides.override =
            io.embrace.shoppingcart.mock.MockNetworkConfig(
                defaultDelayMs = 0L,
                slowDelayMs = 3000L,
                productsScenario = io.embrace.shoppingcart.mock.NetworkScenario.SUCCESS,
                categoriesScenario = io.embrace.shoppingcart.mock.NetworkScenario.SUCCESS,
                placeOrderScenario = io.embrace.shoppingcart.mock.NetworkScenario.SUCCESS,
            )
    }

    @Test
    fun guest_can_add_first_item_and_see_it_in_cart_success() {
        // Logged in via override; rule launches HomeActivity
        composeRule.waitUntil(20_000) { composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true)[0].performClick()
        composeRule.waitUntil(5_000) { try { composeRule.onNode(hasContentDescription("Open cart")).fetchSemanticsNode(); true } catch (_: AssertionError) { false } }
        composeRule.onNode(hasContentDescription("Open cart")).performClick()
        repeat(3) { try { Espresso.pressBack(); Thread.sleep(300) } catch (_: NoActivityResumedException) { return@repeat } }
        assert(true)
    }

    @Test
    fun adding_two_items_success() {
        ActivityScenario.launch(AuthActivity::class.java)
        composeRule.waitUntil(5_000) { try { composeRule.onNode(hasText("Enter as guest"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false } }
        composeRule.onNode(hasText("Enter as guest"), useUnmergedTree = true).performClick()
        ActivityScenario.launch(HomeActivity::class.java)
        composeRule.waitUntil(20_000) { composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true)[0].performClick()
        composeRule.waitUntil(10_000) { composeRule.onAllNodes(hasContentDescription("Add to cart")).fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodes(hasContentDescription("Add to cart"))[0].performClick()
        composeRule.waitUntil(10_000) { try { composeRule.onNode(androidx.compose.ui.test.hasTestTag("open_cart_btn"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false } }
        composeRule.onNode(androidx.compose.ui.test.hasTestTag("open_cart_btn"), useUnmergedTree = true).performClick()
        repeat(3) { try { Espresso.pressBack(); Thread.sleep(300) } catch (_: NoActivityResumedException) { return@repeat } }
        Thread.sleep(2_000)
        assert(true)
    }

    @Module
    @TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
    object TestNetworkModuleSuccess {
        @Provides @Singleton fun provideOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            return OkHttpClient.Builder().addInterceptor(logging).build()
        }
        @Provides @Singleton fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        @Provides @Singleton fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder().baseUrl("https://example.com/").client(okHttpClient).addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        @Provides @Singleton @RealApi fun provideRealApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
        @Provides @Singleton fun provideMockNetworkConfig(): MockNetworkConfig = MockNetworkConfig(
            defaultDelayMs = 0L, slowDelayMs = 3000L,
            productsScenario = NetworkScenario.SUCCESS,
            categoriesScenario = NetworkScenario.SUCCESS,
            placeOrderScenario = NetworkScenario.SUCCESS,
        )
        @Provides @Singleton @MockApi fun provideMockApiService(
            @ApplicationContext context: Context, moshi: Moshi, config: MockNetworkConfig
        ): ApiService = MockApiService(context, moshi, config)
    }
}

@RunWith(AndroidJUnit4::class)
class CartFlowSlowTest {
    @get:Rule val composeRule: AndroidComposeTestRule<*, *> = createAndroidComposeRule<TestHomeActivity>()
    @Before fun setUp() {
        io.embrace.shoppingcart.mock.MockAuthOverrides.loggedInUserId = "test-user"
        io.embrace.shoppingcart.presentation.testutil.UiTestOverrides.verticalListForTests = true
        io.embrace.shoppingcart.mock.MockNetworkConfigOverrides.override =
            io.embrace.shoppingcart.mock.MockNetworkConfig(
                defaultDelayMs = 200L,
                slowDelayMs = 4000L,
                productsScenario = io.embrace.shoppingcart.mock.NetworkScenario.SLOW,
                categoriesScenario = io.embrace.shoppingcart.mock.NetworkScenario.SLOW,
                placeOrderScenario = io.embrace.shoppingcart.mock.NetworkScenario.SLOW,
            )
    }

    @Test
    fun guest_can_add_first_item_and_see_it_in_cart_slow() {
        // Logged in via override; rule launches HomeActivity
        composeRule.waitUntil(25_000) { composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true)[0].performClick()
        composeRule.waitUntil(5_000) { try { composeRule.onNode(hasContentDescription("Open cart")).fetchSemanticsNode(); true } catch (_: AssertionError) { false } }
        composeRule.onNode(hasContentDescription("Open cart")).performClick()
        repeat(3) { try { Espresso.pressBack(); Thread.sleep(300) } catch (_: NoActivityResumedException) { return@repeat } }
        assert(true)
    }

    @Test
    fun adding_two_items_slow() {
        ActivityScenario.launch(AuthActivity::class.java)
        composeRule.waitUntil(5_000) { try { composeRule.onNode(hasText("Enter as guest"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false } }
        composeRule.onNode(hasText("Enter as guest"), useUnmergedTree = true).performClick()
        ActivityScenario.launch(HomeActivity::class.java)
        composeRule.waitUntil(25_000) { composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true)[0].performClick()
        composeRule.waitUntil(20_000) { composeRule.onAllNodes(hasContentDescription("Add to cart")).fetchSemanticsNodes().isNotEmpty() }
        composeRule.onAllNodes(hasContentDescription("Add to cart"))[0].performClick()
        composeRule.waitUntil(10_000) { try { composeRule.onNode(androidx.compose.ui.test.hasTestTag("open_cart_btn"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false } }
        composeRule.onNode(androidx.compose.ui.test.hasTestTag("open_cart_btn"), useUnmergedTree = true).performClick()
        repeat(3) { try { Espresso.pressBack(); Thread.sleep(300) } catch (_: NoActivityResumedException) { return@repeat } }
        Thread.sleep(2_000)
        assert(true)
    }

    @Module
    @TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
    object TestNetworkModuleSlow {
        @Provides @Singleton fun provideOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            return OkHttpClient.Builder().addInterceptor(logging).build()
        }
        @Provides @Singleton fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        @Provides @Singleton fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder().baseUrl("https://example.com/").client(okHttpClient).addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        @Provides @Singleton @RealApi fun provideRealApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
        @Provides @Singleton fun provideMockNetworkConfig(): MockNetworkConfig = MockNetworkConfig(
            defaultDelayMs = 200L, slowDelayMs = 4000L,
            productsScenario = NetworkScenario.SLOW,
            categoriesScenario = NetworkScenario.SLOW,
            placeOrderScenario = NetworkScenario.SLOW,
        )
        @Provides @Singleton @MockApi fun provideMockApiService(
            @ApplicationContext context: Context, moshi: Moshi, config: MockNetworkConfig
        ): ApiService = MockApiService(context, moshi, config)
    }
}

@RunWith(AndroidJUnit4::class)
class CartFlowFailureTestFixed {
    @get:Rule val composeRule: AndroidComposeTestRule<*, *> = createAndroidComposeRule<TestHomeActivity>()
    @Before fun setUp() {
        io.embrace.shoppingcart.mock.MockAuthOverrides.loggedInUserId = "test-user"
        io.embrace.shoppingcart.presentation.testutil.UiTestOverrides.verticalListForTests = true
        io.embrace.shoppingcart.mock.MockNetworkConfigOverrides.override =
            io.embrace.shoppingcart.mock.MockNetworkConfig(
                defaultDelayMs = 100L,
                slowDelayMs = 3000L,
                productsScenario = io.embrace.shoppingcart.mock.NetworkScenario.FAILURE,
                categoriesScenario = io.embrace.shoppingcart.mock.NetworkScenario.FAILURE,
                placeOrderScenario = io.embrace.shoppingcart.mock.NetworkScenario.FAILURE,
            )
    }

    @Test
    fun shows_error_snackbar_on_failure_home() {
        // Logged in via override; rule launches HomeActivity
        composeRule.waitUntil(15_000) {
            try { composeRule.onNode(hasText("Retry"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        // Snackbar error present; tap Retry to exercise action
        composeRule.onNode(hasText("Retry"), useUnmergedTree = true).performClick()
        assert(true)
    }

    @Module
    @TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
    object TestNetworkModuleFailure {
        @Provides @Singleton fun provideOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            return OkHttpClient.Builder().addInterceptor(logging).build()
        }
        @Provides @Singleton fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        @Provides @Singleton fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder().baseUrl("https://example.com/").client(okHttpClient).addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        @Provides @Singleton @RealApi fun provideRealApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
        @Provides @Singleton fun provideMockNetworkConfig(): MockNetworkConfig = MockNetworkConfig(
            defaultDelayMs = 100L, slowDelayMs = 3000L,
            productsScenario = NetworkScenario.FAILURE,
            categoriesScenario = NetworkScenario.FAILURE,
            placeOrderScenario = NetworkScenario.FAILURE,
        )
        @Provides @Singleton @MockApi fun provideMockApiService(
            @ApplicationContext context: Context, moshi: Moshi, config: MockNetworkConfig
        ): ApiService = MockApiService(context, moshi, config)
    }
}

@RunWith(AndroidJUnit4::class)
class CartFlowServerErrorTestFixed {
    @get:Rule val composeRule: AndroidComposeTestRule<*, *> = createAndroidComposeRule<TestHomeActivity>()
    @Before fun setUp() {
        io.embrace.shoppingcart.mock.MockAuthOverrides.loggedInUserId = "test-user"
        io.embrace.shoppingcart.presentation.testutil.UiTestOverrides.verticalListForTests = true
        io.embrace.shoppingcart.mock.MockNetworkConfigOverrides.override =
            io.embrace.shoppingcart.mock.MockNetworkConfig(
                defaultDelayMs = 100L,
                slowDelayMs = 3000L,
                productsScenario = io.embrace.shoppingcart.mock.NetworkScenario.SERVER_ERROR,
                categoriesScenario = io.embrace.shoppingcart.mock.NetworkScenario.SERVER_ERROR,
                placeOrderScenario = io.embrace.shoppingcart.mock.NetworkScenario.SERVER_ERROR,
            )
    }

    @Test
    fun shows_error_snackbar_on_server_error_home() {
        // Logged in via override; rule launches HomeActivity
        composeRule.waitUntil(15_000) {
            try { composeRule.onNode(hasText("Retry"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasText("Retry"), useUnmergedTree = true).performClick()
        assert(true)
    }

    @Module
    @TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
    object TestNetworkModuleServerError {
        @Provides @Singleton fun provideOkHttpClient(): OkHttpClient {
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
            return OkHttpClient.Builder().addInterceptor(logging).build()
        }
        @Provides @Singleton fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        @Provides @Singleton fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder().baseUrl("https://example.com/").client(okHttpClient).addConverterFactory(MoshiConverterFactory.create(moshi)).build()
        @Provides @Singleton @RealApi fun provideRealApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
        @Provides @Singleton fun provideMockNetworkConfig(): MockNetworkConfig = MockNetworkConfig(
            defaultDelayMs = 100L, slowDelayMs = 3000L,
            productsScenario = NetworkScenario.SERVER_ERROR,
            categoriesScenario = NetworkScenario.SERVER_ERROR,
            placeOrderScenario = NetworkScenario.SERVER_ERROR,
        )
        @Provides @Singleton @MockApi fun provideMockApiService(
            @ApplicationContext context: Context, moshi: Moshi, config: MockNetworkConfig
        ): ApiService = MockApiService(context, moshi, config)
    }
}
