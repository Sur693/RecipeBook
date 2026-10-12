package com.hfad.recipebook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun RecipeItem(
    imageRes: String?,
    name: String,
    category: String,
    area: String,
    quantity: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        AsyncImage(
            model = imageRes,
            contentDescription = name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = if (area != "") Color.Black.copy(alpha = 0.2f) else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            if (area != "") {
                Text(
                    text = area,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
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
                        color = Color(0xFFF6F8FC)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = quantity.toString(),
                            fontSize = 18.sp,
                            color = Color(0xFFF6F8FC)
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun RecipeItemPreview() {
    RecipeItem(
        imageRes = "https://themealdb.com",
        name = "Запеченная картошка",
        category = "Вегетарианское",
        area = "Русская кухня",
        quantity = 4,
        onClick = {  }
    )
}