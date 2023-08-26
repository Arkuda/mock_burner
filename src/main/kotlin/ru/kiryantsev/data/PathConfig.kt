package ru.kiryantsev.data

import kotlinx.serialization.Serializable

@Serializable
data class PathConfig(
    val pathMask : String,
    val pathMethod: HttpMethodType,
    val requestBodyData: String?,
    val pathToFile: String,
    val responseCode: Int,
)