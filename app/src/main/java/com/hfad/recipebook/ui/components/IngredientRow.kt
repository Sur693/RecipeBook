package com.hfad.recipebook.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun IngredientRow(
    ingredient: String,
    measure: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp),
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

@Preview
@Composable
fun IngredientRowPreview() {
    IngredientRow(
        ingredient = "Желатин",
        measure = "2 чайные ложки"
    )
}