package ru.kiryantsev

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    requestCatcher()
}

fun Application.requestCatcher() {
    routing {
        get("{...}") {
            RequestParser.parse(this)
        }

        post("{...}") {
            RequestParser.parse(this)
        }

        put("{...}") {
            RequestParser.parse(this)
        }

        delete("{...}") {
            RequestParser.parse(this)
        }
    }
    RequestCacheController.reloadConfig()
}


fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeaders { true }
        anyHost()
    }
}

