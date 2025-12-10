package com.xuziyi.toutiaoandroid.common.extensions

class ApiException(val code: Int, message: String) : Exception(message)
class ApiDataNullException(message: String = "API 响应成功但 data 字段为空") : Exception(message)