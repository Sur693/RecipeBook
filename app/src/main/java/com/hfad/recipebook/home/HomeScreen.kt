package com.hfad.recipebook.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.recipebook.R
import com.hfad.recipebook.ui.theme.Black
import com.hfad.recipebook.ui.theme.BlueSoft
import com.hfad.recipebook.ui.theme.GrayDark
import com.hfad.recipebook.ui.theme.GrayLight
import com.hfad.recipebook.ui.theme.White


@Composable
internal fun HomeScreen(
    navigateToDetailRecipe: (String) -> Unit = {},
    navigateToFilter: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    viewModel: HomeViewModel
){
    val state = viewModel.homeScreenState.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()
    val activeFilter = viewModel.activeFilter.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
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

            IconButton( onClick = navigateToFilter){
                Image(
                    painter = painterResource(R.drawable.tune_60dp_000000_fill0_wght400_grad0_opsz48),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
            }

            IconButton( onClick = navigateToFavorite){
                Image(
                    painter = painterResource(R.drawable.favorite_60dp_000000_fill0_wght400_grad0_opsz48),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )
            }
        }

        if (activeFilter.value != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter: ${activeFilter.value?.second}",
                    fontSize = 16.sp,
                    color = GrayDark
                )

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = { viewModel.clearFilter() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Clear",
                        fontSize = 16.sp,
                        color = GrayDark
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "Recipes you can make",
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ){
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ){
                    items(state.value){ recipe ->
                        RecipeItem(
                            imageRes = recipe.imageRes,
                            name = recipe.title,
                            category = recipe.category,
                            quantity = recipe.quantity,
                            area = recipe.area,
                            onClick = {navigateToDetailRecipe(recipe.id)}
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
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GrayLight,  // цвет фона когда активно
            unfocusedContainerColor = GrayLight, // цвет фона когда неактивно
            focusedTextColor = Color.Black, // цвет текста
            unfocusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun RecipeItem(
    imageRes: String?,
    name: String,
    category: String,
    area: String,
    quantity: Int,
    onClick: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageRes)
                .crossfade(true)
                .build(),
            contentDescription = name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp)) // скругление углов
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart) // прижать к верху картинки
                    .padding(16.dp)
                    .background(
                        color = Black.copy(alpha = 0.2f), // полупрозрачный чёрный
                        shape = RoundedCornerShape(16.dp) // скругление углов
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp) // внутренние отступы
            ) {
                Text(
                    text = area,
                    color = White,
                    fontSize = 16.sp
                )
            }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    text = name,
                    fontSize = 24.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(0.9f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category,
                        fontSize = 18.sp,
                        color = BlueSoft
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.list_alt_40dp_ffffff_fill0_wght400_grad0_opsz40),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)

                        )

                        Spacer(modifier = Modifier.width(2.dp))

                        Text(
                            text = quantity.toString(),
                            fontSize = 18.sp,
                            color = BlueSoft
                        )
                    }
                }
            }
        }
    }
}