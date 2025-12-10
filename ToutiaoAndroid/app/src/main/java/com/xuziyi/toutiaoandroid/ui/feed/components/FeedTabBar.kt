package com.xuziyi.toutiaoandroid.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xuziyi.toutiaoandroid.R
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

@Composable
fun FeedTabBar(
    tabs: List<FeedTabItem>,
    selectedIndex: Int,
    hasNewFollowing: Boolean = false,
    showChannelManager: Boolean = true,
    onTabSelected: (Int) -> Unit,
    onListenClick: () -> Unit = {}
) {
    val activeColor = Color(0xFFFF4D4F)
    val inactiveColor = Color.Black

    val tabListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    //用户点击 tab 后三横线出现
    var menuVisible by remember { mutableStateOf(false) }

    //点击 Tab 自动居中逻辑
    LaunchedEffect(selectedIndex) {
        coroutineScope.launch {

            val layoutInfo = tabListState.layoutInfo
            val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == selectedIndex }
            val viewportCenter = layoutInfo.viewportEndOffset / 2

            if (itemInfo == null) {
                tabListState.animateScrollToItem(selectedIndex)
            }
            awaitFrame()

            val newInfo =
                tabListState.layoutInfo.visibleItemsInfo.find { it.index == selectedIndex }
                    ?: return@launch

            val itemCenter = newInfo.offset + newInfo.size / 2
            val diff = itemCenter - viewportCenter

            tabListState.animateScrollBy(diff.toFloat())
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 左侧标签区域
        Box(modifier = Modifier.weight(1f)) {

            LazyRow(
                state = tabListState,
                contentPadding = PaddingValues(start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(tabs) { index, tab ->

                    val isSelected = (index == selectedIndex)

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onTabSelected(index)
                                menuVisible = true   //点击 tab → 三横线 & 渐变出现
                            }
                            .padding(end = if (index == tabs.lastIndex) 30.dp else 0.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Box(contentAlignment = Alignment.Center) {

                            Text(
                                text = tab.title,
                                fontSize = 19.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) activeColor else inactiveColor,
                            )

                            val showDot =
                                (tab.title == "关注" && hasNewFollowing) || tab.showDot

                            if (showDot) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                        .size(8.dp)
                                        .background(activeColor, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(activeColor, CircleShape)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }

            //初始状态（未点击）：显示白色遮挡条 —— 完全盖住尾部标签
            if (!menuVisible) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(17.dp)     // 白挡板宽度，可调
                        .fillMaxHeight()
                        .background(Color.White)  // 不透明白色
                )
            }

            //点击 Tab 后（menuVisible=true）：显示渐变遮挡条
            if (menuVisible) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(18.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, Color.White)
                            )
                        )
                )
            }
        }

        // ⭐ 三横线按钮（只在点击 Tab 后出现）
        if (showChannelManager && menuVisible) {
            Icon(
                painter = painterResource(id = R.drawable.ic_menu),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
                    .clickable { }
            )
        }

        // 听新闻按钮（始终显示）
        Icon(
            painter = painterResource(id = R.drawable.ic_listen),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp)
                .clickable(onClick = onListenClick)
        )
    }
}
