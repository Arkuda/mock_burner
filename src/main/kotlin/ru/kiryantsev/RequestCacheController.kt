package ru.kiryantsev

import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import ru.kiryantsev.data.ConfigFile
import ru.kiryantsev.data.HttpMethodType
import ru.kiryantsev.data.PathConfig
import java.io.File
import java.util.UUID
import kotlin.system.exitProcess

object RequestCacheController {


    private lateinit var config: ConfigFile

    private val configFile = File("${Util.currentWorkingDir}/config.json")

    val serverToProxy get() = config.serverToProxy


    fun tryGetResponseBody(method: HttpMethod, fulluri: String, body: String?): Pair<String, Int>? {
        val readyBody = tryFindPathConfig(method, fulluri, body)
        return if (readyBody != null) {
            return Pair(File("${Util.currentWorkingDir}/${readyBody.pathToFile}").readText(), readyBody.responseCode)
        } else {
            null
        }
    }

    fun saveResponse(
        method: HttpMethod,
        fulluri: String,
        requestBody: String?,
        responseBody: String,
        resposeCode: Int
    ) {
        val thisFilename = if (fulluri == "/" && method == HttpMethod.Post) {
            "PROCEDURE/" + procedureReqParamsToFilePath(responseBody)
        } else {
            reqParamsToFilePath(method, fulluri)
        }

        val thisConfig = PathConfig(
            pathMask = fulluri,
            pathMethod = method.toOwnMethod(),
            requestBodyData = requestBody,
            responseCode = resposeCode,
            pathToFile = thisFilename
        )

        saveResponseBody(Util.currentWorkingDir + "/" + thisFilename, responseBody)

        val newConfig = config.copy(pathConfigs = mutableListOf(thisConfig).apply {
            config.pathConfigs?.let {
                addAll(it)
            }
        })

        config = newConfig
        saveConfig()
    }

    private fun saveResponseBody(thisFilename: String, responseBody: String) {
        val file = File(thisFilename)
        val dir = file.parentFile
        if (!dir.exists()) {
            dir.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        file.writeText(responseBody)
    }


    fun reloadConfig() {
        if (!configFile.exists()) {
            println("config.json not found, creating config.json")
            println("change serverToProxy and restart app")
            File("${Util.currentWorkingDir}/config.json").apply {
                createNewFile()
                val sampleConfig = Json.encodeToString(ConfigFile(null, "http://testserver.com", null))
                writeText(sampleConfig)
            }
            exitProcess(1)
        }
        val configLines = configFile.inputStream()
        config = Json.decodeFromStream<ConfigFile>(configLines)
    }

    private fun tryFindPathConfig(method: HttpMethod, fulluri: String, body: String?): PathConfig? {
        return if (fulluri == "/" && method == HttpMethod.Post) {
            //its rpc or graphql
            config.pathConfigs?.find {
                it.requestBodyData == body
            }
        } else {
            //its regular http request
            config.pathConfigs?.find {
                Regex.fromLiteral(it.pathMask).matches(fulluri)
            }

        }
    }

    private fun saveConfig() {
        if (!configFile.exists()) {
            configFile.createNewFile()
        }
        configFile.writeText(Json.encodeToString(config))
    }


    private fun reqParamsToFilePath(method: HttpMethod, fulluri: String): String =
        "${Util.currentWorkingDir}/${method.value}/${fulluri.encodeToFile()}"

    private fun procedureReqParamsToFilePath(body: String): String =
        body.replace(" ", "").substring(10).trim().encodeToFile() + UUID.randomUUID()

    private fun String.encodeToFile(): String {
        return replace("?", "@") // todo add other forbidden symbols
    }

    private fun HttpMethod.toOwnMethod(): HttpMethodType = when (HttpMethod.parse(value)) {
        HttpMethod.Get -> HttpMethodType.GET
        HttpMethod.Post -> HttpMethodType.POST
        HttpMethod.Put -> HttpMethodType.PUT
        HttpMethod.Delete -> HttpMethodType.DELETE
        else -> HttpMethodType.POST
    }
}

