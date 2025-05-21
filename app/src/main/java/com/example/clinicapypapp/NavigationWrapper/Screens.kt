package com.example.clinicapypapp.NavigationWrapper

import kotlinx.serialization.Serializable

@Serializable
object LoginDest

@Serializable
data class MainDest(
    val idUsuario: Int,
)

@Serializable
data class ServicesDest(
    val idUsuario: Int,
    val idSeccion: Int,
    val idEspecialista: Int,
    val sectionName: String
)

@Serializable
data class UsuarioDest(
    val idUsuario: Int
)

@Serializable
data class CitaDest(val idUsuario: Int, val idSeccion: Int, val idServicio: Int, val idEspecialista: Int, val sectionName: String)

@Serializable
data class CitasUsuarioDest(val idUsuario: Int)

@Serializable
object QuienSomosDest

@Serializable
data class MisDatosDest(val idUsuario: Int)