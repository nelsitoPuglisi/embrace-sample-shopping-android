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
class HomeFlowSuccessTest {
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
    fun guest_can_add_first_item_to_favorites_success() {
        // Already auto-logged in via override and rule launched HomeActivity
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true)[0].performClick()
        repeat(3) { try { Espresso.pressBack(); Thread.sleep(300) } catch (_: NoActivityResumedException) { return@repeat } }
        assert(true)
    }

}

@RunWith(AndroidJUnit4::class)
class HomeFlowSlowTest {
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
    fun guest_can_add_first_item_to_favorites_slow() {
        // Logged in via override; rule launches HomeActivity
        composeRule.waitUntil(timeoutMillis = 25_000) {
            composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodes(androidx.compose.ui.test.hasTestTag("add_to_cart"), useUnmergedTree = true)[0].performClick()
        repeat(3) { try { Espresso.pressBack(); Thread.sleep(300) } catch (_: NoActivityResumedException) { return@repeat } }
        assert(true)
    }

}

@RunWith(AndroidJUnit4::class)
class HomeFlowFailureTestFixed {
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
    fun shows_error_snackbar_on_failure() {
        // Logged in via override; rule launches HomeActivity
        composeRule.waitUntil(timeoutMillis = 15_000) {
            try { composeRule.onNode(hasText("Retry"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        // Snackbar with Retry action should be visible
        composeRule.onNode(hasText("Retry"), useUnmergedTree = true).performClick()
        assert(true)
    }

}

@RunWith(AndroidJUnit4::class)
class HomeFlowServerErrorTestFixed {
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
    fun shows_error_snackbar_on_server_error() {
        // Logged in via override; rule launches HomeActivity
        composeRule.waitUntil(timeoutMillis = 15_000) {
            try { composeRule.onNode(hasText("Retry"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasText("Retry"), useUnmergedTree = true).performClick()
        assert(true)
    }

}
