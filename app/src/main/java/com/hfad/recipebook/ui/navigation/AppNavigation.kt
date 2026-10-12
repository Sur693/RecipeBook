package com.hfad.recipebook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hfad.recipebook.ui.detail.RecipeDetailScreen
import com.hfad.recipebook.ui.detail.RecipeDetailViewModel
import com.hfad.recipebook.ui.favorite.FavoriteScreen
import com.hfad.recipebook.ui.favorite.FavoriteViewModel
import com.hfad.recipebook.ui.filter.FilterScreen
import com.hfad.recipebook.ui.home.HomeScreen
import com.hfad.recipebook.ui.home.HomeViewModel

object Screen {
    const val Home = "home"
    const val Favorite = "favorite"
    const val Filter = "filter"
    const val Detail = "detail/{recipeId}"

    fun createDetailRoute(recipeId: String) = "detail/$recipeId"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // кроссплатформенный инструмент для открытия ссылок на YouTube
    val uriHandler = LocalUriHandler.current

    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier
    ) {
        // главный экран
        composable(route = Screen.Home) {
            val homeViewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                navigateToDetail = { id ->
                    navController.navigate(Screen.createDetailRoute(id))
                },
                navigateToFilter = { navController.navigate(Screen.Filter) },
                navigateToFavorite = { navController.navigate(Screen.Favorite) },
                viewModel = homeViewModel
            )
        }

        // экран избранного
        composable(route = Screen.Favorite) {
            val favoriteViewModel: FavoriteViewModel = hiltViewModel()

            FavoriteScreen(
                onBackClick = { navController.popBackStack() },
                navigateToDetailRecipe = { id ->
                    navController.navigate(Screen.createDetailRoute(id))
                },
                viewModel = favoriteViewModel
            )
        }

        // экран фильтров
        composable(route = Screen.Filter) { backStackEntry ->
            // находим запись главного экрана в стеке навигации
            val homeEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Home)
            }
            // берем ViewModel, которая принадлежит главному экрану
            val homeViewModel: HomeViewModel = hiltViewModel(homeEntry)

            FilterScreen(
                onBackClick = { navController.popBackStack() },
                onApply = { (filterType, value) ->
                    homeViewModel.searchByFilter(value, filterType)
                    navController.popBackStack()
                }
            )
        }

        // экран деталей рецепта
        composable(
            route = Screen.Detail, // регистрируем путь вида "detail/{recipeId}"
            arguments = listOf(navArgument("recipeId") {
                type = NavType.StringType
            })
        ) {
            val detailViewModel: RecipeDetailViewModel = hiltViewModel()

            RecipeDetailScreen(
                onBackClick = { navController.popBackStack() },
                onOpenYoutubeClick = { videoId ->
                    if (videoId.isNotBlank()) uriHandler.openUri("https://www.youtube.com/watch?v=$videoId")

                },
                viewModel = detailViewModel
            )
        }
    }
}