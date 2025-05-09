package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Servicio(
    val idServicio: Int? = null,
    val nombreServicio: String,
    val textoExplicativo: String? = null,
    val duracion: Int? = null,
    val seccion: Seccion? = null
)