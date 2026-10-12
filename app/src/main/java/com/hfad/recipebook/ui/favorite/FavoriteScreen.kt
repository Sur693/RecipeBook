package com.hfad.recipebook.ui.favorite


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfad.recipebook.ui.components.RecipeItem
import com.hfad.recipebook.ui.components.TopAppBar

@Composable
fun FavoriteScreen(
    onBackClick: () -> Unit,
    navigateToDetailRecipe: (String) -> Unit,
    viewModel: FavoriteViewModel
) {
    val state = viewModel.favoriteScreenState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.padding(30.dp)
        ) {
            TopAppBar(
                title = "Favorites",
                onBackClick = onBackClick
            )
        }

        // контейнер для отображения списка рецептов
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (state.value.isEmpty()) {
                // если пользователь еще ничего не сохранил
                Text(
                    text = "No favorite recipes yet",
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // если рецепты в базе есть
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(state.value) { recipe ->
                        RecipeItem(
                            imageRes = recipe.imageRes,
                            name = recipe.title,
                            category = recipe.category,
                            quantity = recipe.quantity,
                            area = recipe.area,
                            onClick = { navigateToDetailRecipe(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}