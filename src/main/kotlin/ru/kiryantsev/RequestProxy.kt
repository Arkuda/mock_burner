package ru.kiryantsev

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.BodyProgress.Plugin.install
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.request.*

class RequestProxy {

    companion object {

        private val cookieStorage = ConstantCookiesStorage()

        private val client = HttpClient {
            install(HttpCookies) {
                storage = cookieStorage
            }
        }

        suspend fun proxyRequest(req: ApplicationRequest, body: String): HttpResponse {
//            req.cookies.rawCookies // todo WORK WITH COOKIES
            return client.request(
                (RequestCacheController.serverToProxy ?: "") + "/" + req.uri
            ) {
                method = req.httpMethod
                req.headers.entries().forEach {
                    headers.appendAll(it.key, it.value)
                }
                setBody(body)
            }
        }
    }


}