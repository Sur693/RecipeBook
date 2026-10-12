package com.hfad.recipebook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.hfad.recipebook.RecipeBookApp
import com.hfad.recipebook.data.converter.DataConverter
import com.hfad.recipebook.ui.detail.RecipeDetailScreen
import com.hfad.recipebook.ui.detail.RecipeDetailViewModel
import com.hfad.recipebook.ui.detail.RecipeDetailViewModelFactory
import com.hfad.recipebook.ui.favorite.FavoriteScreen
import com.hfad.recipebook.ui.favorite.FavoriteViewModel
import com.hfad.recipebook.ui.favorite.FavoriteViewModelFactory
import com.hfad.recipebook.ui.filter.FilterScreen
import com.hfad.recipebook.ui.home.HomeScreen
import com.hfad.recipebook.ui.home.HomeViewModel
import com.hfad.recipebook.ui.home.HomeViewModelFactory


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

    val app = LocalContext.current.applicationContext as RecipeBookApp

    // кроссплатформенный инструмент для открытия ссылок на YouTube
    val uriHandler = LocalUriHandler.current

    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier
    ) {
        // главный экран
        composable(route = Screen.Home) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(converter = DataConverter())
            )

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
            val favoriteViewModel: FavoriteViewModel = viewModel(
                factory = FavoriteViewModelFactory(app.favoritesRepository)
            )

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
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(DataConverter())
            )

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
            }) // ждем строго текстовый id
        ) { backStackEntry -> // backStackEntry содержит историю и данные текущего экрана

            // достаем из прилетевшей ссылки текстовый id
            val recipeId = backStackEntry.arguments?.read { getStringOrNull("recipeId") } ?: ""

            val detailViewModel: RecipeDetailViewModel = viewModel(
                factory = RecipeDetailViewModelFactory(recipeId, app.favoritesRepository)
            )

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