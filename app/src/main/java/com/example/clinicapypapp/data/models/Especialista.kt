package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Especialista(
    val idEspecialista: Int? = null, // ID autogenerado
    val nombreEspecialista: String,
    val apellidosEspecialista: String
)