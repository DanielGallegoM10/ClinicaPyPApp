package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Seccion(
    val idSeccion: Int? = null, // ID autogenerado
    val imagenSeccion: Int,
    val nombreSeccion: String, // Basado en @Column(nullable = false)
    val especialistaId: Int? = null // ID del especialista relacionado
    // Si la API devuelve el objeto Especialista anidado, necesitarías definir una clase Especialista simplificada aquí
    // @Serializable data class EspecialistaRelacion(val idEspecialista: Int, val nombreEspecialista: String)
    // val especialista: EspecialistaRelacion? = null
)