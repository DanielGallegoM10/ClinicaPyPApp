package com.example.clinicapypapp.data.api

import com.example.clinicapypapp.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ApiService(private val httpClient: HttpClient) { // Recibe el cliente Ktor configurado

    //Función para obtener todos los usuarios (GET /api/usuarios)
    suspend fun getAllUsuarios(): List<Usuario> {
        // Usa httpClient.get para hacer una petición GET
        return httpClient.get {
            // Define la URL del endpoint
            url {

                appendPathSegments("api", "usuarios")
            }
        }.body() // Deserializa la respuesta JSON
    }

    suspend fun getUsuarioById(id: Int): Usuario? {
        // Usa httpClient.get para hacer una petición GET
        return httpClient.get {

            url {
                appendPathSegments("api", "usuarios", id.toString())
            }
        }.body<Usuario?>()  // Deserializa la respuesta JSON a un objeto Usuario
    }

    suspend fun createUsuario(usuario: Usuario): Usuario {
        // Usa httpClient.post para hacer una petición POST
        return httpClient.post {
            url {
                appendPathSegments("api", "usuarios")
            }
            // Indica que el cuerpo de la petición es JSON
            contentType(ContentType.Application.Json)
            // Pone el objeto Usuario en el cuerpo de la petición, serializado como JSON por Ktor
            setBody(usuario)
        }.body() // Deserializa la respuesta JSON (el usuario creado con ID) a un objeto Usuario
    }

    suspend fun updateUsuario(id: Int, usuario: Usuario): Usuario {
        // Usa httpClient.put para hacer una petición PUT
        return httpClient.put {
            url {
                appendPathSegments("api", "usuarios", id.toString()) // Esto resulta en BASE_URL/api/usuarios/ID
            }
            // Indica que el cuerpo de la petición es JSON
            contentType(ContentType.Application.Json)
            // Pone el objeto Usuario actualizado en el cuerpo de la petición
            setBody(usuario)
        }.body() // Deserializa la respuesta JSON (el usuario actualizado) a un objeto Usuario
    }

    suspend fun deleteUsuario(id: Int) {
        // Usa httpClient.delete para hacer una petición DELETE
        httpClient.delete {
            url {
                appendPathSegments("api", "usuarios", id.toString()) // Esto resulta en BASE_URL/api/usuarios/ID
            }
            // Las peticiones DELETE rara vez tienen un cuerpo de petición
        }
        // NOTA: No usamos .body() aquí porque normalmente la respuesta no tiene un cuerpo JSON relevante,
        // solo esperamos que la petición se complete sin lanzar una excepción HTTP Client (ej. 404 si no existe, 500 si hay error).
        // Si tu API devuelve un mensaje de confirmación en JSON, podrías usar .body<TuClaseDeRespuesta>() aquí.
    }



}
