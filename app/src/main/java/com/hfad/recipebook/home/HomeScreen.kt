package com.hfad.recipebook.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.ui.res.painterResource
import com.hfad.recipebook.R



@Composable
internal fun HomeScreen(
    navigateToDetailRecipe: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
){
    val state = viewModel.homeScreenState.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ){
        Header() //название + кнопка избранного + кнопка настройки

        Spacer(Modifier.height(40.dp))


        //SearchBar() поисковая строка + фильтры (ингридиенты категории и страны)

        Text(
            text = "Recipes you can make",
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Расстояние между элементами
        ){
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
}

@Composable
fun Header(){
    Button( onClick = {}
    ) {
        Image(
            painter = painterResource(R.drawable.heart),
            null
        )
    }
    Text(
        text = "Fridge",
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    )
    Button( onClick = {}
    ) {
        Image(
            painter = painterResource(R.drawable.setting),
            null

        )
    }
}

@Composable
fun RecipeItem(
    imageRes: Int,
    name: String,
    category: String,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(text = category, fontSize = 14.sp, color = Color.White)
            Text(text = name, fontWeight = FontWeight.Bold)
        }
    }
}


