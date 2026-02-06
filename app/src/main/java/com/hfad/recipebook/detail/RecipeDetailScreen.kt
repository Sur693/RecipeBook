package com.hfad.recipebook.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.hfad.recipebook.R
import com.hfad.recipebook.ui.theme.Black
import com.hfad.recipebook.ui.theme.White

@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: String,
    onApply: () -> Unit,
    viewModel: RecipeDetailViewModel
) {

    val favorites = viewModel.favorites.collectAsState()
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
            Column (modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AsyncImage(
                        model = currentState.value.imageRes,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart) // прижать к верху картинки
                            .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                            .background(
                                color = White.copy(alpha = 0.5f), // полупрозрачный чёрный
                                shape = RoundedCornerShape(16.dp)       // скругление углов
                            )
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                        ) {
                            Image(
                                painter = painterResource(R.drawable.arrow_back_60dp_000000_fill0_wght400_grad0_opsz48),
                                contentDescription = "back",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd) // прижать к верху картинки
                            .padding(top = 30.dp, start = 16.dp, end = 16.dp)
                            .background(
                                color = White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)       // скругление углов
                            )
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite() }
                        ) {
                            Image(
                                painter = painterResource(
                                    if (recipeId in favorites.value)
                                        R.drawable.heart_check_60dp_000000_fill0_wght400_grad0_opsz48
                                    else
                                        R.drawable.favorite_60dp_000000_fill0_wght400_grad0_opsz48
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp) // заезд на картинку
                        .background(
                            color = White,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        )
                        .padding(start = 16.dp, top = 24.dp)
                ) {
                    Column()
                    {
                        Text(
                            text = currentState.value.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            maxLines = 2,
                            modifier = Modifier.padding(end = 24.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "${currentState.value.category} • ${currentState.value.area}",
                            fontSize = 14.sp,
                            color = Black
                        )

                        Spacer(Modifier.height(24.dp))

                        LazyColumn {
                            item {
                                Row{
                                    Text(
                                        text = "Ingredients",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Black
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        text = currentState.value.quantity.toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraLight,
                                        color = Black
                                    )
                                }


                            }

                            items(
                            currentState.value.ingredients.zip(currentState.value.measures)
                            ) { (ingredient, measure) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ingredient,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 16.sp,
                                        maxLines = 2,
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = measure,
                                        fontSize = 16.sp,
                                    )
                                }
                            }

                            item {
                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = "Instructions",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Black
                                )

                                Box(
                                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
                                ) {
                                    if (currentState.youtubeVideoId != null) {
                                        YoutubeButton(
                                            videoId = currentState.youtubeVideoId,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(60.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

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
    }
}

@Composable
fun YoutubeButton(
    videoId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .clickable {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=$videoId")
                )
                context.startActivity(intent)
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.play_arrow_60dp_ffffff_fill0_wght400_grad0_opsz48),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Watch on YouTube",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
