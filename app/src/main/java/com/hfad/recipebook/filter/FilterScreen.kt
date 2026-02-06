package com.hfad.recipebook.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hfad.recipebook.R
import com.hfad.recipebook.ui.theme.Black


@Composable
fun FilterScreen(
    navController: NavController,
    onApply: (Pair<FilterType, String>) -> Unit
) {
    var selectedFilter by remember { mutableStateOf<FilterType?>(null) }
    var selectedItem by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding((30.dp))
            .fillMaxSize()
    ) {

        if (selectedFilter == null){
            Box(
                modifier = Modifier
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
                    text = "Search by",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilterOption("Categories") { selectedFilter = FilterType.CATEGORY }
            FilterOption("Areas") { selectedFilter = FilterType.AREA }
            FilterOption("Ingredients") { selectedFilter = FilterType.INGREDIENT }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                IconButton(
                    onClick = {
                        selectedFilter = null
                        searchText = ""
                        selectedItem = null      },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Image(
                        painter = painterResource(R.drawable.arrow_back_60dp_000000_fill0_wght400_grad0_opsz48),
                        contentDescription = "back",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = when (selectedFilter!!) {
                        FilterType.CATEGORY -> "Categories"
                        FilterType.AREA -> "Areas"
                        FilterType.INGREDIENT -> "Ingredients"
                    },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            FilterList(
                selectedFilter!!,
                selectedItem,
                searchQuery = searchText,
                {selectedItem = it},
                {value -> onApply(value)}
            )
        }
    }
}



@Composable
fun FilterOption(
    text: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Row{
            Image(
                painter = painterResource(R.drawable.chevron_right_60dp_000000_fill0_wght400_grad0_opsz48),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Black
            )
        }
    }
}

@Composable
fun FilterList(
    type: FilterType,
    selectedItem: String?,
    searchQuery: String,
    onItemSelected: (String) -> Unit,
    onApply: (Pair<FilterType, String>) -> Unit
) {
    val items = when (type) {
        FilterType.CATEGORY -> FilterData.categories
        FilterType.AREA -> FilterData.areas
        FilterType.INGREDIENT -> FilterData.ingredients
    }

    val filteredItems = if (searchQuery.isEmpty()) {
        items
    } else {
        items.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn {
        items(filteredItems) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemSelected (item)
                        onApply(type to item)
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = item == selectedItem,
                    onClick = {
                        onItemSelected(item)
                        onApply(type to item)
                    }
                )
                Text(
                    text = item,
                    fontSize = 20.sp,
                )

            }
        }
    }
}