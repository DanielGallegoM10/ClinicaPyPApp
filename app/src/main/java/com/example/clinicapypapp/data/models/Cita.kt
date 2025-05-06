package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Cita(
    val idCita: Int? = null,
    val servicio: Servicio? = null,
    val seccion: Seccion? = null,
    val usuario: Usuario? = null,
    val texto: String,
    val fecha: String,
    val hora: String
)