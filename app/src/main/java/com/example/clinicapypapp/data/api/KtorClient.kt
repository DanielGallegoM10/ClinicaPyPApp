package com.example.clinicapypapp.data.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.plugins.logging.* // Opcional: para ver los logs de las peticiones
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {

    private const val BASE_URL = "http://212.227.145.78:8080/" //Aqui pongo IP y puerto, se construye la url base

    val httpClient = HttpClient(Android) {
        engine {
        }

        install(DefaultRequest) {
            url(BASE_URL) //Configura el cliente para usar esta URL como base
        }

        // Instala el plugin de Negociación de Contenido para JSON
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
}