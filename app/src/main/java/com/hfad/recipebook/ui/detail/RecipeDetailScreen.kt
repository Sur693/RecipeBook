package com.hfad.recipebook.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hfad.recipebook.ui.components.IngredientRow
import com.hfad.recipebook.ui.components.YoutubeButton
import androidx.compose.foundation.lazy.items

@Composable
fun RecipeDetailScreen(
    onBackClick: () -> Unit,
    onOpenYoutubeClick: (String) -> Unit,
    viewModel: RecipeDetailViewModel
) {
    val isFavorite = viewModel.isFavoriteState.collectAsState()
    val state = viewModel.detailScreenState.collectAsState()

    when (val currentState = state.value) {
        is RecipeDetailUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is RecipeDetailUiState.Recipe -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Картинка блюда из сети через Coil 3
                    AsyncImage(
                        model = currentState.value.imageRes,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f),
                        contentScale = ContentScale.Crop
                    )

                    // Кнопка Назад
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .statusBarsPadding()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Кнопка Сердечко
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        IconButton(
                            // передаем весь объект рецепта в базу данных через репозиторий
                            onClick = { viewModel.toggleFavorite(currentState.value) }
                        ) {
                            Icon(
                                imageVector = if (isFavorite.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                modifier = Modifier.size(28.dp),
                                tint =  Color.Black
                            )
                        }
                    }
                }

                // описание
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .padding(start = 16.dp, top = 24.dp)
                ) {
                    Column {
                        // название блюда
                        Text(
                            text = currentState.value.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            maxLines = 2,
                            modifier = Modifier.padding(end = 24.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        // категория и регион блюда
                        Text(
                            text = "${currentState.value.category} • ${currentState.value.area}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(24.dp))

                        // прокручиваемый список ингредиентов и инструкции
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(bottom = 60.dp)
                        ) {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Ingredients",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    // количество ингредиентов
                                    Text(
                                        text = currentState.value.quantity.toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraLight,
                                        color = Color.Black
                                    )
                                }
                            }

                            // список ингредиентов
                            items(
                                currentState.value.ingredients.zip(currentState.value.measures)
                            ) { (ingredient, measure) ->
                                IngredientRow(
                                    ingredient = ingredient,
                                    measure = measure
                                )
                            }

                            item {
                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = "Instructions",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )

                                Box(
                                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
                                ) {
                                    // кнопка YouTube
                                    if (currentState.youtubeVideoId != null) {
                                        YoutubeButton(
                                            onClick = { onOpenYoutubeClick(currentState.youtubeVideoId) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(60.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                // текст инструкции приготовления
                                Text(
                                    text = currentState.value.instruction,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        is RecipeDetailUiState.Error -> {
            // экран ошибки, если пропал интернет
            Column {
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Something went wrong. Please check your internet connection.",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
