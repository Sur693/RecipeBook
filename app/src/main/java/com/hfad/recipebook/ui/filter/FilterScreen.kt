package com.hfad.recipebook.ui.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hfad.recipebook.data.filter.FilterData
import com.hfad.recipebook.domain.model.FilterType
import com.hfad.recipebook.ui.components.TopAppBar

@Composable
fun FilterScreen(
    onBackClick: () -> Unit,
    onApply: (Pair<FilterType, String>) -> Unit
) {
    var selectedFilter by remember { mutableStateOf<FilterType?>(null) }
    var selectedItem by remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(30.dp)
            .fillMaxSize()
    ) {
        // категории фильтрации
        if (selectedFilter == null) {
            TopAppBar(
                title = "Search by",
                onBackClick = onBackClick
            )

            Spacer(Modifier.height(16.dp))

            // кнопки переключения этапа фильтрации
            FilterOption("Categories") { selectedFilter = FilterType.CATEGORY }
            FilterOption("Areas") { selectedFilter = FilterType.AREA }
            FilterOption("Ingredients") { selectedFilter = FilterType.INGREDIENT }
        } else {
            // Элементы внутри категории
            TopAppBar(
                title = when (selectedFilter!!) {
                    FilterType.CATEGORY -> "Categories"
                    FilterType.AREA -> "Areas"
                    FilterType.INGREDIENT -> "Ingredients"
                },
                onBackClick = {
                    selectedFilter = null
                    searchText = ""
                    selectedItem = null
                }
            )

            Spacer(Modifier.height(16.dp))

            // локальный поиск внутри элементов
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

            // прокручиваемый список
            FilterList(
                type = selectedFilter!!,
                selectedItem = selectedItem,
                searchQuery = searchText,
                onItemSelected = { selectedItem = it },
                onApply = onApply
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
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
                        onItemSelected(item)
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
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}