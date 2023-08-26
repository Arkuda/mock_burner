package ru.kiryantsev.data

import kotlinx.serialization.Serializable


@Serializable
data class ConfigFile(
    val dontProxy : List<String>?,
    val serverToProxy: String?,
    val pathConfigs: List<PathConfig>?,
)
