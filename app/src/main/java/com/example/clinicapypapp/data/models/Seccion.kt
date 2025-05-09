package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Seccion(
    val idSeccion: Int? = null,
    val imagenSeccion: String,
    val nombreSeccion: String,
    val especialista: Especialista? = null
)