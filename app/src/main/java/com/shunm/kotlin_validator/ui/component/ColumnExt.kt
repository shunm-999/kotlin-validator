package com.shunm.kotlin_validator.ui.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable

@Composable
fun <T> ColumnScope.items(
    items: List<T>,
    itemContent: @Composable ColumnScope.(T) -> Unit
) {
    items.forEach { item ->
        itemContent(item)
    }
}