package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val idUsuario: Int? = null,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val dni: String,
    val nombreUsuario: String,
    var contrasenia: String
)