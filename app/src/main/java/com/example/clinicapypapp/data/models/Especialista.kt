package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Especialista(
    val idEspecialista: Int? = null,
    val nombreEspecialista: String,
    val apellidosEspecialista: String
)