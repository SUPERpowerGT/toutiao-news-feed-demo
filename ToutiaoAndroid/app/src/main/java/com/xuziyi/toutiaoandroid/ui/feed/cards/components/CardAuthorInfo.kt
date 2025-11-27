package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.xuziyi.toutiaoandroid.R
import com.xuziyi.toutiaoandroid.common.util.TimeAgoFormatter
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun CardAuthorInfo(item: FeedItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        AsyncImage(
            model = item.author.avatarUrl,
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape),
            placeholder = painterResource(R.drawable.ic_default_avatar),
            error = painterResource(R.drawable.ic_default_avatar),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = item.author.name,
            fontSize = 14.sp,
            color = Color(0xFF777777)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = TimeAgoFormatter.format(item.publishTime),
            fontSize = 14.sp,
            color = Color(0xFFAAAAAA)
        )
    }
}
