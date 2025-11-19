package com.xuziyi.toutiaoandroid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun FeedCard(item: FeedItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text("SEQ: ${item.seq_id}", style = MaterialTheme.typography.titleMedium)
        Text("News ID: ${item.news_id}")
        Text("Type: ${item.display_type}")
        Text("Time: ${item.publish_time}")
    }
}
