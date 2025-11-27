package com.xuziyi.toutiaoandroid.domain.model

sealed class FeedCardType(val type: String) {
    object Text : FeedCardType("text")
    object Image : FeedCardType("image")
    object Video : FeedCardType("video")
    //object Official : FeedCardType("official")
    object OfficialTop : FeedCardType("official_top")
    //object Gallery : FeedCardType("gallery")
    //object Ad : FeedCardType("ad")
}