package com.example.clinicapypapp.data.api

import com.example.clinicapypapp.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.*
import io.ktor.http.*

class ApiService(private val httpClient: HttpClient) {

    suspend fun getAllUsuarios(): List<Usuario> {
        return httpClient.get {
            url {

                appendPathSegments("api", "usuarios")
            }
        }.body()
    }

    suspend fun getUsuarioById(id: Int): Usuario? {
        return httpClient.get {

            url {
                appendPathSegments("api", "usuarios", id.toString())
            }
        }.body<Usuario?>()
    }

    suspend fun createUsuario(usuario: Usuario): Usuario {
        return httpClient.post {
            url {
                appendPathSegments("api", "usuarios")
            }
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }.body()
    }

    suspend fun updateUsuario(id: Int, usuario: Usuario): Usuario {
        return httpClient.put {
            url {
                appendPathSegments(
                    "api",
                    "usuarios",
                    id.toString()
                )
            }
            contentType(ContentType.Application.Json)
            setBody(usuario)
        }.body()
    }

    suspend fun deleteUsuario(id: Int) {
        httpClient.delete {
            url {
                appendPathSegments(
                    "api",
                    "usuarios",
                    id.toString()
                )
            }
        }
    }

    suspend fun createSeccion(seccion: Seccion): Seccion {
        return httpClient.post {
            url {
                appendPathSegments("api", "secciones")
            }
            contentType(ContentType.Application.Json)
            setBody(seccion)
        }.body()
    }

    suspend fun getAllSecciones(): List<Seccion> {
        return httpClient.get {
            url {
                appendPathSegments("api", "secciones")
            }
        }.body<List<Seccion>>()
    }

    suspend fun getServiciosPorSeccion(idSeccion: Int): List<Servicio> {
        return httpClient.get {
            url {
                appendPathSegments("api", "servicios", "seccion", idSeccion.toString())
            }
        }.body<List<Servicio>>()

    }

    suspend fun createCita(cita: Cita): Cita {
        return httpClient.post {
            url { appendPathSegments("api", "citas") }
            contentType(ContentType.Application.Json)
            setBody(cita)
        }.body<Cita>()
    }

    suspend fun deleteCita(idCita: Int) {
        httpClient.delete {
            url { appendPathSegments("api", "citas", idCita.toString()) }
        }
    }

    suspend fun getMisCitas(idUsuario: Int): List<Cita> {
        return httpClient.get {
            url {
                appendPathSegments("api", "citas", "usuario", idUsuario.toString())
            }
        }.body<List<Cita>>()
    }

    suspend fun getCitasbyEspecialistaAndFecha(idEspecialista: Int, fecha: String): List<String> {
        return httpClient.get{
            url {
                appendPathSegments("api", "citas", "ocupadas", "especialista", idEspecialista.toString(), "fecha", fecha.toString())
            }
        }.body<List<String>>()
    }
}
