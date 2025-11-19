package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.xuziyi.toutiaoandroid.ui.components.FeedCard

@Composable
fun FeedScreen(viewModel: FeedViewModel = FeedViewModel()) {
    val feedList by viewModel.feedList.collectAsState()

    LazyColumn {
        items(feedList) { item ->
            FeedCard(item)
        }

        item {
            Button(onClick = { viewModel.loadMore() }) {
                Text("加载更多")
            }
        }
    }
}


