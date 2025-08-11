package io.embrace.shoppingcart.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.embrace.shoppingcart.presentation.search.SearchScreen

object Routes {
    const val Search = "search"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.Search) {
        composable(Routes.Search) { SearchScreen() }
    }
}


