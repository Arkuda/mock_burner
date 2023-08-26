package ru.kiryantsev

import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

object RequestParser {

    private val saver = RequestProxy()

    //check it contains in files
    // if not - proxy request and save and response
    // if have - send "cached" request


    suspend fun parse(pipelineContext: PipelineContext<Unit, ApplicationCall>) {
        with(pipelineContext.call.request) {
//            val headers = headers.entries()
            val method = HttpMethod.parse(local.method.value)
            val fulluri = local.uri
//            val cookies = cookies.rawCookies
            val body = call.receiveText()
            val readyResponse = RequestCacheController.tryGetResponseBody(method = method, fulluri = fulluri, body = body)

            if (readyResponse != null) {
                //send "cached" request
                pipelineContext.call.respond(status = HttpStatusCode(readyResponse.second,""), readyResponse.first)
            } else {
                // proxy request and save and response
              val response = RequestProxy.proxyRequest(call.request, body)
                RequestCacheController.saveResponse(
                    method,
                    fulluri,
                    body,
                    response.bodyAsText(),
                    response.status.value,
                )
            }
        }
    }

}


