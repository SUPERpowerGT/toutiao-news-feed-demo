package com.xuziyi.toutiaoandroid.ui.feed.cards.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuziyi.toutiaoandroid.R
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun CardActionRow(item: FeedItem) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        //点赞
        Icon(
            painter = painterResource(R.drawable.ic_like),
            contentDescription = null,
            tint = Color(0xFF777777),
            modifier = Modifier.size(14.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = item.stats.likeCount.toString(),
            fontSize = 14.sp,
            color = Color(0xFF777777)
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 更多 ···
        Icon(
            painter = painterResource(R.drawable.ic_more_vert),
            contentDescription = null,
            tint = Color(0xFF777777),
            modifier = Modifier.size(18.dp)
        )
    }
}
