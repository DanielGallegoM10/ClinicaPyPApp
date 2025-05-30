package com.example.clinicapypapp.data.api

import com.example.clinicapypapp.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.*
import io.ktor.http.*

class ApiService(private val httpClient: HttpClient) {

    // Funciones para interactuar con la API

    //Funcion para obtener todos los usuarios
    suspend fun getAllUsuarios(): List<Usuario> {
        return httpClient.get {
            url {
                appendPathSegments("api", "usuarios")
            }
        }.body()
    }

    //Funcion para obtener un usuario por su id
    suspend fun getUsuarioById(id: Int): Usuario? {
        return httpClient.get {

            url {
                appendPathSegments("api", "usuarios", id.toString())
            }
        }.body<Usuario?>()
    }

    //Funcion para actualizar un usuario
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

    //funcion para obtener todas las secciones
    suspend fun getAllSecciones(): List<Seccion> {
        return httpClient.get {
            url {
                appendPathSegments("api", "secciones")
            }
        }.body<List<Seccion>>()
    }

    //funcion para obtener todos los servicios de una sección
    suspend fun getServiciosPorSeccion(idSeccion: Int): List<Servicio> {
        return httpClient.get {
            url {
                appendPathSegments("api", "servicios", "seccion", idSeccion.toString())
            }
        }.body<List<Servicio>>()

    }

    //funcion para crear una cita
    suspend fun createCita(cita: Cita): Cita {
        return httpClient.post {
            url { appendPathSegments("api", "citas") }
            contentType(ContentType.Application.Json)
            setBody(cita)
        }.body<Cita>()
    }

    //funcion para eliminar o anular una cita
    suspend fun deleteCita(idCita: Int) {
        httpClient.delete {
            url { appendPathSegments("api", "citas", idCita.toString()) }
        }
    }

    //funcion para obtener las citas de un usuario
    suspend fun getMisCitas(idUsuario: Int): List<Cita> {
        return httpClient.get {
            url {
                appendPathSegments("api", "citas", "usuario", idUsuario.toString())
            }
        }.body<List<Cita>>()
    }

    //funcion para obtener las citas de un especialista en una fecha determinada
    suspend fun getCitasbyEspecialistaAndFecha(idEspecialista: Int, fecha: String): List<String> {
        return httpClient.get{
            url {
                appendPathSegments("api", "citas", "ocupadas", "especialista", idEspecialista.toString(), "fecha", fecha.toString())
            }
        }.body<List<String>>()
    }
}
