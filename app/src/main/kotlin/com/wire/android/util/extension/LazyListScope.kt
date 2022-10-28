package com.wire.android.ui.home.conversationslist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.wire.android.ui.home.conversationslist.common.FolderHeader

@OptIn(ExperimentalFoundationApi::class)
inline fun <T, K : Any> LazyListScope.folderWithElements(
    header: String,
    headerColor: Color? = null,
    items: Map<K, T>,
    crossinline divider: @Composable () -> Unit = {},
    crossinline factory: @Composable (T) -> Unit
) {
    val list = items.entries.toList()

    if (items.isNotEmpty()) {
        item(key = "header:$header") {
            FolderHeader(
                name = header,
                modifier = Modifier
                    .fillMaxWidth()
                    .run { headerColor?.let { background(color = it) } ?: this }
                    .animateItemPlacement()
            )
        }
        itemsIndexed(
            items = list,
            key = { _: Int, item: Map.Entry<K, T> -> item.key })
        { index: Int, item: Map.Entry<K, T> ->
            Box(modifier = Modifier
                    .wrapContentSize()
                    .animateItemPlacement()
            ) {
                factory(item.value)
                if (index < list.lastIndex)
                    divider()
            }
        }
    }
}