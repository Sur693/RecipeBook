package com.hfad.recipebook.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfad.recipebook.ui.components.RecipeItem

@Composable
fun HomeScreen(
    navigateToDetail: (String) -> Unit = {},
    navigateToFilter: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    viewModel: HomeViewModel
) {
    val state = viewModel.homeScreenState.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()
    val activeFilter = viewModel.activeFilter.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
        // заголовок приложения
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "Fridge",
                fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp
            )
        }

        // строка поиска и управляющие кнопки
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                query = searchQuery.value,
                onQueryChange = { viewModel.searchRecipes(it) },
                modifier = Modifier.weight(1f)
            )

            // кнопка перехода к фильтрам
            IconButton(onClick = navigateToFilter) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filter",
                    modifier = Modifier.size(28.dp)
                )
            }

            // кнопка перехода в избранное
            IconButton(onClick = navigateToFavorite) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorites",
                    modifier = Modifier.size(28.dp),
                    tint = Color.Black
                )
            }
        }

        // блок отображения активного фильтра
        if (activeFilter.value != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter: ${activeFilter.value?.second}",
                    fontSize = 16.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = { viewModel.clearFilter() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Clear",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // подзаголовок списка
        Text(
            text = "Recipes you can make",
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        // контейнер для списка или индикатора загрузки
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.value) { recipe ->
                        RecipeItem(
                            imageRes = recipe.imageRes,
                            name = recipe.title,
                            category = recipe.category,
                            quantity = recipe.quantity,
                            area = recipe.area,
                            onClick = { navigateToDetail(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search recipes") },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),

        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
        },

        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear Text",
                        tint = Color.Gray
                    )
                }
            }
        },

        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF0F0F0),
            unfocusedContainerColor = Color(0xFFF0F0F0),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
