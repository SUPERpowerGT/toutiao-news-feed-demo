package com.xuziyi.toutiaoandroid.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xuziyi.toutiaoandroid.data.repo.FeedRepository
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val repo = FeedRepository()

    private val _feedList = MutableStateFlow<List<FeedItem>>(emptyList())
    val feedList = _feedList.asStateFlow()

    private var cursor: String? = null

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            val list = repo.loadFeedList(cursor)
            _feedList.value = list
        }
    }

    fun loadMore() {
        loadFeed()
    }
}


