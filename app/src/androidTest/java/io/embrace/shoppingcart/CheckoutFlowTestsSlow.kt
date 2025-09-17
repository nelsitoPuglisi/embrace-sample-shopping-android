package io.embrace.shoppingcart

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.embrace.shoppingcart.mock.MockAuthOverrides
import io.embrace.shoppingcart.mock.MockNetworkConfig
import io.embrace.shoppingcart.mock.MockNetworkConfigOverrides
import io.embrace.shoppingcart.mock.NetworkScenario
import io.embrace.shoppingcart.presentation.testutil.UiTestOverrides
import io.embrace.shoppingcart.ui.home.HomeActivity
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CheckoutFlowTestsSlow {

    @get:Rule
    val composeRule: AndroidComposeTestRule<*, *> = createAndroidComposeRule<HomeActivity>()

    companion object Companion {
        @JvmStatic
        @BeforeClass
        fun setupClass() {
            MockAuthOverrides.loggedInUserId = "test-user"
            UiTestOverrides.verticalListForTests = true
            MockNetworkConfigOverrides.override =
                MockNetworkConfig(
                    defaultDelayMs = 0L,
                    slowDelayMs = 3000L,
                    productsScenario = NetworkScenario.SUCCESS,
                    categoriesScenario = NetworkScenario.SUCCESS,
                    placeOrderScenario = NetworkScenario.SLOW,
                )
        }
    }

    @Test
    fun full_checkout_happy_path() {
        // Add first product to cart
        composeRule.waitUntil(30_000) {
            composeRule.onAllNodes(hasTestTag("add_to_cart"), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodes(hasTestTag("add_to_cart"), useUnmergedTree = true)[0]
            .performClick()

        // Open cart
        composeRule.waitUntil(10_000) {
            try {
                composeRule.onNode(hasTestTag("open_cart_btn"), useUnmergedTree = true)
                    .fetchSemanticsNode()
                ; true
            } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasTestTag("open_cart_btn"), useUnmergedTree = true).performClick()

        // Continue to checkout
        composeRule.waitUntil(30_000) {
            try { composeRule.onNode(hasTestTag("cart_item"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasTestTag("checkout_btn"), useUnmergedTree = true).performClick()

        // Next: Shipping
        composeRule.waitUntil(10_000) {
            try { composeRule.onNode(hasTestTag("to_shipping_btn"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasTestTag("to_shipping_btn"), useUnmergedTree = true).performClick()

        // Fill shipping fields
        composeRule.onNode(hasTestTag("name_field"), useUnmergedTree = true).performTextInput("John Tester")
        composeRule.onNode(hasTestTag("street_field"), useUnmergedTree = true).performTextInput("123 Test St")
        composeRule.onNode(hasTestTag("city_field"), useUnmergedTree = true).performTextInput("Testville")
        composeRule.onNode(hasTestTag("state_field"), useUnmergedTree = true).performTextInput("TS")
        composeRule.onNode(hasTestTag("zip_field"), useUnmergedTree = true).performTextInput("12345")
        composeRule.onNode(hasTestTag("country_field"), useUnmergedTree = true).performTextInput("Testland")

        // Next: Payment
        composeRule.onNode(hasTestTag("to_payment_btn"), useUnmergedTree = true).performClick()

        // Add payment method
        composeRule.waitUntil(10_000) {
            try { composeRule.onNode(hasTestTag("add_payment_btn"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasTestTag("add_payment_btn"), useUnmergedTree = true).performClick()

        // Fill payment method details
        composeRule.waitUntil(10_000) {
            try { composeRule.onNode(hasTestTag("brand_field"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasTestTag("brand_field"), useUnmergedTree = true).performTextInput("VISA")
        composeRule.onNode(hasTestTag("last4_field"), useUnmergedTree = true).performTextInput("4242")
        composeRule.onNode(hasTestTag("month_field"), useUnmergedTree = true).performTextInput("12")
        composeRule.onNode(hasTestTag("year_field"), useUnmergedTree = true).performTextInput("2028")
        composeRule.onNode(hasTestTag("save_btn"), useUnmergedTree = true).performClick()

        // Go back to Payment screen (dismiss keyboard then activity)
        Espresso.pressBack()
        Thread.sleep(200)
        Espresso.pressBack()

        // Select the newly added payment method (row button)
        composeRule.waitUntil(15_000) {
            composeRule.onAllNodes(hasTestTag("visa_added"), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodes(hasTestTag("visa_added"), useUnmergedTree = true)[0].performClick()

        // Next: Confirm
        composeRule.waitUntil(15_000) {
            try { composeRule.onNode(hasTestTag("to_confirm_btn"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasTestTag("to_confirm_btn"), useUnmergedTree = true).performClick()

        // Place Order
        composeRule.waitUntil(10_000) {
            try { composeRule.onNode(hasTestTag("place_order_btn"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }
        composeRule.onNode(hasTestTag("place_order_btn"), useUnmergedTree = true).performClick()

        // Optionally, wait for confirmation text
        composeRule.waitUntil(10_000) {
            try { composeRule.onNode(hasTestTag("order_placed"), useUnmergedTree = true).fetchSemanticsNode(); true } catch (_: AssertionError) { false }
        }

        composeRule.onNode(hasTestTag("finish_btn"), useUnmergedTree = true).performClick()
        repeat(3) { try { Espresso.pressBack(); Thread.sleep(300) } catch (_: NoActivityResumedException) { return@repeat } }
        Thread.sleep(2_000)
        assert(true)
    }
}
