package com.xuziyi.toutiaoandroid.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class BackendUrlTest {

    @Test
    fun `relative media path uses configured backend URL`() {
        assertEquals(
            "http://10.0.2.2:8080/media/demo-video.mp4",
            resolveBackendUrl("/media/demo-video.mp4")
        )
    }

    @Test
    fun `absolute media URL remains unchanged`() {
        assertEquals(
            "https://cdn.example.com/video.mp4",
            resolveBackendUrl("https://cdn.example.com/video.mp4")
        )
    }
}
