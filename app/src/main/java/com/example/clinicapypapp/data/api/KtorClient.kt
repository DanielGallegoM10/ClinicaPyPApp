package com.example.clinicapypapp.data.api

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.client.plugins.logging.* // Opcional: para ver los logs de las peticiones
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient { // Usamos un objeto singleton para tener una sola instancia

    private const val BASE_URL = "http://212.227.145.78:8080/" // ¡CAMBIA ESTO POR TU IP REAL Y PUERTO!
    // Si después configuras Nginx con dominio y HTTPS, cambiarías esto a "https://tu.dominio.com/"

    val httpClient = HttpClient(Android) {
        // Configura el motor para Android
        engine {
            // Puedes añadir configuraciones específicas del motor Android aquí si las necesitas
        }

        // Instala el plugin de Negociación de Contenido para JSON
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Ignora campos en el JSON de la API que no estén en tus modelos Kotlin
                isLenient = true // Permite ciertos formatos JSON no estrictos
            })
        }

        // Opcional: Instala el plugin de Logging (muy útil para debug)
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL // Loggea todos los detalles de la petición y respuesta (útil en desarrollo)
//            // Para producción, considera LogLevel.NONE o LogLevel.INFO
//        }

        // Puedes añadir otros plugins aquí: timeouts, caché, interceptores, etc.
    }
}