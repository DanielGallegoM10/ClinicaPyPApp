package com.example.clinicapypapp.data.models

import kotlinx.serialization.Serializable
// Nota: Si la API envía el ID aunque sea null en POST, puede que necesites @Required
// Si la API envía el ID como 0 en lugar de null para entidades nuevas, ajusta el valor por defecto
@Serializable
data class Usuario(
    val idUsuario: Int? = null, // ID autogenerado en la BD, puede ser null al crear uno nuevo
    val nombre: String,
    val apellidos: String,
    val email: String,
    val dni: String,
    val nombreUsuario: String,
    val contrasenia: String
)