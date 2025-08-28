package io.embrace.shoppingcart

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import io.embrace.shoppingcart.ui.auth.AuthActivity
import io.embrace.shoppingcart.ui.home.HomeActivity
import okhttp3.internal.wait
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartFlowTest {

    @get:Rule
    val composeRule: ComposeTestRule = createEmptyComposeRule()

    @Test
    fun guest_can_add_first_item_and_see_it_in_cart() {
        // 1) Launch Auth and enter as guest
        ActivityScenario.launch(AuthActivity::class.java)
        // Wait until the button is present and click via Semantics (avoids touch injection)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            try {
                composeRule.onNode(hasText("Enter as guest"), useUnmergedTree = true)
                    .fetchSemanticsNode()
                true
            } catch (_: AssertionError) {
                false
            }
        }
        composeRule.onNode(hasText("Enter as guest"), useUnmergedTree = true)
            .performClick()

        // 2) Launch Home (now authenticated) and add first item to cart
        ActivityScenario.launch(HomeActivity::class.java)

        // Ensure at least one product is visible and tap its "Add to cart"
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasContentDescription("Add to cart"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodes(hasContentDescription("Add to cart"))[0]
            .performClick()

        // 3) Open the cart from the top app bar
        composeRule.waitUntil(timeoutMillis = 5_000) {
            try {
                composeRule.onNode(hasContentDescription("Open cart"))
                    .fetchSemanticsNode()
                true
            } catch (_: AssertionError) {
                false
            }
        }
        composeRule.onNode(hasContentDescription("Open cart"))
            .performClick()

        // 4) Wait 10s before verifying cart reflects the added item
        Thread.sleep(10_000)//wait so embrace can send the session
        // Then verify with an additional waitUntil to reduce flakiness
        composeRule.waitUntil(timeoutMillis = 10_000) {
            try {
                composeRule.onNodeWithText("Items: 1").fetchSemanticsNode()
                true
            } catch (_: AssertionError) {
                false
            }
        }

        Thread.sleep(10_000)//wait so embrace can send the session

        // Final assertion for clarity
        composeRule.onNodeWithText("Items: 1").assertExists()
    }
}
