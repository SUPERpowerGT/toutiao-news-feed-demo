package com.xuziyi.toutiaoandroid.data.remote

internal const val BACKEND_BASE_URL = "http://10.0.2.2:8080/"

fun resolveBackendUrl(url: String): String {
    if (url.startsWith("http://", ignoreCase = true) ||
        url.startsWith("https://", ignoreCase = true)
    ) {
        return url
    }

    return BACKEND_BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
}
