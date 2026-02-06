package com.hfad.recipebook.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.recipebook.R
import com.hfad.recipebook.data.converters.DataConverter
import com.hfad.recipebook.ui.theme.Black
import com.hfad.recipebook.ui.theme.BlueSoft
import com.hfad.recipebook.ui.theme.White


@Composable
fun FavoriteScreen(
    navController: NavController,
    navigateToDetailRecipe: (String) -> Unit = {},
    onApply: () -> Unit,
){
    val context = LocalContext.current

    val viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(
            context = context,
            converter = DataConverter()
        )
    )

    val state = viewModel.favoriteScreenState.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()

    Column {
        Box (
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Image(
                    painter = painterResource(R.drawable.arrow_back_60dp_000000_fill0_wght400_grad0_opsz48),
                    contentDescription = "back",
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                text = "Favorites",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
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
                    shape = RoundedCornerShape(16.dp)       // скругление углов
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