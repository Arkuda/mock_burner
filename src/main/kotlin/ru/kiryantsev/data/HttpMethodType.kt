package ru.kiryantsev.data

import kotlinx.serialization.Serializable

@Serializable
enum class HttpMethodType {
    GET, POST, PUT, DELETE
}