package com.example.clinicapypapp.data.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.plugins.logging.* // Opcional: para ver los logs de las peticiones
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient { // Usamos un objeto singleton para tener una sola instancia

    private const val BASE_URL = "http://212.227.145.78:8080/" // ¡TU IP REAL Y PUERTO!

    val httpClient = HttpClient(Android) {
        // Configura el motor para Android
        engine {
            // ...
        }

        // === AÑADIDO: Plugin para configurar peticiones por defecto, incluyendo la URL base ===
        install(DefaultRequest) {
            url(BASE_URL) // <<== Configura el cliente para usar esta URL como base
            // Si necesitas añadir headers por defecto (ej. para autenticación), también irían aquí
            // header(HttpHeaders.Authorization, "Bearer your_token")
        }
        // =====================================================================================

        // Instala el plugin de Negociación de Contenido para JSON
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        // Opcional: Instala el plugin de Logging (muy útil para debug)
//        install(Logging) { // <<== Si lo tenías comentado, descoméntalo temporalmente para ver los logs de red
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL // Ponlo en ALL para ver qué URL está intentando conectar
//        }

        // Puedes añadir otros plugins aquí
    }
}