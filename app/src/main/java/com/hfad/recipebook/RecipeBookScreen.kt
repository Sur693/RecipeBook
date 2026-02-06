package com.hfad.recipebook


import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hfad.recipebook.data.converters.DataConverter
import com.hfad.recipebook.filter.FilterScreen
import com.hfad.recipebook.detail.RecipeDetailScreen
import com.hfad.recipebook.detail.RecipeDetailViewModelFactory
import com.hfad.recipebook.favorite.FavoriteScreen
import com.hfad.recipebook.favorite.FavoriteViewModelFactory
import com.hfad.recipebook.favorite.FavoritesRepository
import com.hfad.recipebook.home.HomeScreen
import com.hfad.recipebook.home.HomeViewModel
import com.hfad.recipebook.home.HomeViewModelFactory

sealed class Screen(val route: String){
    object Home : Screen("home")
    object Filter : Screen("filters")
    object Detail : Screen("details/{recipeId}") {
        fun createRoute(recipeId: String) = "details/$recipeId"
    }
    object Favorite : Screen("favorite")
}

@Composable
fun RecipeBookScreen(){
    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(converter = DataConverter())
    )

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                navigateToDetailRecipe = { recipeId ->
                    navController.navigate(Screen.Detail.createRoute(recipeId))
                },
                navigateToFilter = {
                    navController.navigate(Screen.Filter.route)
                },
                navigateToFavorite = {
                    navController.navigate(Screen.Favorite.route)
                }
            )
        }

        composable (Screen.Favorite.route){
            FavoriteScreen(
                navController = navController,
                onApply = {
                    navController.popBackStack()
                },
                navigateToDetailRecipe = { recipeId ->
                    navController.navigate(Screen.Detail.createRoute(recipeId))
                },
            )
        }

        composable(Screen.Filter.route) {
            FilterScreen(
                navController = navController,
                onApply = { filter ->
                    viewModel.searchByFilter(filter.second, filter.first)
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.Detail.route,  // "detail/{recipeId}"
            arguments = listOf(
                navArgument("recipeId") { type = NavType.StringType }  // объявляем аргумент
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId")  // достаём ID

            RecipeDetailScreen(
                navController = navController,
                recipeId = recipeId ?: "",  // передаём в DetailScreen
                onApply = {
                    navController.popBackStack()
                },
                viewModel = viewModel(
                    factory = RecipeDetailViewModelFactory(
                        context = context,
                        recipeId = recipeId ?: "",
                        converter = DataConverter()
                    )
                )
            )
        }
    }
}