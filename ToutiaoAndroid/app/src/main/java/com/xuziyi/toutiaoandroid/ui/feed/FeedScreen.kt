package com.xuziyi.toutiaoandroid.ui.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onOpenDetail: (Long) -> Unit = {}
) {
    val state = viewModel.state.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            else -> {
                FeedList(
                    items = state.items,
                    onItemClick = onOpenDetail
                )
            }
        }
    }
}
