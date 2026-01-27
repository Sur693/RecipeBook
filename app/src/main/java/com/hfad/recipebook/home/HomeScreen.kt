package com.hfad.recipebook.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hfad.recipebook.R
import com.hfad.recipebook.data.converters.DataConverter


@Composable
internal fun HomeScreen(
    navigateToDetailRecipe: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(converter = DataConverter())
    )
){
    val state = viewModel.homeScreenState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .fillMaxSize()
    ){
        item{
            Header() //название + кнопка избранного + кнопка настройки
            Spacer(Modifier.height(40.dp))

            //SearchBar() поисковая строка + фильтры (ингридиенты категории и страны)

            Text(
                text = "Recipes you can make",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }
        items(state.value){ recipe ->
            RecipeItem(
                imageRes = recipe.imageRes,
                name = recipe.title,
                category = recipe.category,
                onClick = {navigateToDetailRecipe(recipe.id)}
            )

        }
    }
}

@Composable
fun Header(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton( onClick = {}){
            Image(
                painter = painterResource(R.drawable.favorite_60dp_000000_fill0_wght400_grad0_opsz48),
                contentDescription = null,
                modifier = Modifier.size(90.dp)
            )

        }


        Text(
            text = "Fridge",
            fontWeight = FontWeight.SemiBold,
            fontSize = 40.sp
        )


        IconButton( onClick = {},) {
            Image(
                painter = painterResource(R.drawable.settings_60dp_000000_fill0_wght400_grad0_opsz48),
                null,
                modifier = Modifier.size(90.dp)
            )
        }
    }
}

@Composable
fun RecipeItem(
    imageRes: String?,
    name: String,
    category: String,
    onClick: () -> Unit
){
    Log.d("DEBUG", "RecipeItem called! imageRes = $imageRes")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageRes)
                .crossfade(true)
                .listener(
                    onStart = {
                        Log.d("Coil", "START loading: $imageRes")
                    },
                    onSuccess = { _, _ ->
                        Log.d("Coil", "SUCCESS: $imageRes")
                    },
                    onError = { _, result ->
                        Log.e("Coil", "ERROR loading: $imageRes")
                        Log.e("Coil", "Error: ${result.throwable.message}")
                        result.throwable.printStackTrace()
                    }
                )
                .build(),
            contentDescription = name,
            modifier = Modifier
                .size(90.dp)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = category, fontSize = 14.sp, color = Color.Black)
            Text(text = name, fontWeight = FontWeight.Bold)
        }
    }
}


