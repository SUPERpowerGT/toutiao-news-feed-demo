package com.xuziyi.toutiaoandroid.ui.detail

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xuziyi.toutiaoandroid.data.remote.resolveBackendUrl
import com.xuziyi.toutiaoandroid.data.remote.dto.NewsDetailDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(
    viewModel: NewsDetailViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新闻详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        when (val current = state) {
            NewsDetailUiState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is NewsDetailUiState.Error -> Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(current.message)
                androidx.compose.material3.TextButton(onClick = viewModel::load) { Text("重试") }
            }

            is NewsDetailUiState.Success -> DetailContent(current.detail, Modifier.padding(padding))
        }
    }
}

@Composable
private fun DetailContent(detail: NewsDetailDto, modifier: Modifier = Modifier) {
    val videoUrl = detail.media.firstOrNull { it.mediaType == "video" }?.url?.let(::resolveBackendUrl)
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = detail.title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(detail.author.name, style = MaterialTheme.typography.labelLarge)
            Text("${detail.stats.likeCount} 赞 · ${detail.stats.commentCount} 评论")
        }
        if (!videoUrl.isNullOrBlank()) {
            LifecycleVideoPlayer(videoUrl, Modifier.fillMaxWidth().height(230.dp).padding(top = 12.dp))
        }
        AndroidView(
            modifier = Modifier.fillMaxWidth().weight(1f),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.loadsImagesAutomatically = true
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
            update = { webView ->
                val styledHtml = """
                    <html><head><meta name="viewport" content="width=device-width, initial-scale=1"/>
                    <style>body{font-size:18px;line-height:1.75;padding:8px 14px;color:#222}img{max-width:100%;height:auto}</style>
                    </head><body>${detail.contentHtml.orEmpty()}</body></html>
                """.trimIndent()
                webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
            }
        )
    }
}

@Composable
private fun LifecycleVideoPlayer(url: String, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val player = remember(url) { mutableStateOf<VideoView?>(null) }
    AndroidView(
        modifier = modifier,
        factory = { context ->
            VideoView(context).also { videoView ->
                player.value = videoView
                videoView.setMediaController(MediaController(context).apply { setAnchorView(videoView) })
                videoView.setVideoPath(url)
                videoView.setOnPreparedListener { it.isLooping = false; videoView.start() }
            }
        }
    )
    DisposableEffect(lifecycleOwner, url) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> player.value?.start()
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> player.value?.pause()
                Lifecycle.Event.ON_DESTROY -> player.value?.stopPlayback()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.value?.stopPlayback()
            player.value = null
        }
    }
}
