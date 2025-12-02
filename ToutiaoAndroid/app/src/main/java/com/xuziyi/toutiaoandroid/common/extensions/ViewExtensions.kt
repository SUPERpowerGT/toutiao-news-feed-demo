package com.xuziyi.toutiaoandroid.common.extensions

// TODO: 放通用扩展函数（比如 Modifier 的扩展）
// 示例：ApiError.kt (或类似名称的文件)
class ApiException(val code: Int, message: String) : Exception(message)
class ApiDataNullException(message: String = "API 响应成功但 data 字段为空") : Exception(message)