package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Servicio(
    val idServicio: Int? = null, // ID autogenerado
    val nombreServicio: String,
    val textoExplicativo: String? = null,
    val duracion: Int? = null,
    val seccionId: Int? = null // ID de la sección relacionada
    // Si la API devuelve el objeto Seccion anidado, necesitarías definir una clase Seccion simplificada aquí
    // @Serializable data class SeccionRelacion(val idSeccion: Int, val nombreSeccion: String)
    // val seccion: SeccionRelacion? = null
)