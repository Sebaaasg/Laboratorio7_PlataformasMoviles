package com.example.laboratorio7.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object HttpClientFactory {
    fun create(): HttpClient {
        return HttpClient(CIO.create()) {
            // Muestra en Logcat cada petición y su respuesta, sirve para revisar qué devuelve el API
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.ANDROID
            }
            // Convierte el JSON de la respuesta a nuestros DTOs
            install(ContentNegotiation) {
                json(
                    json = Json {
                        // El API manda campos que no usamos (origin, episode, etc.), así no da error
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
}
