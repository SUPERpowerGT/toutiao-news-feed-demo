package com.xuziyi.toutiaoandroid.domain.model

@JvmInline
value class FeedContentType(val value: String) {
    companion object {
        const val TEXT = "text"
        const val IMAGE = "image"
        const val VIDEO = "video"
        const val GALLERY = "gallery"
    }
}
