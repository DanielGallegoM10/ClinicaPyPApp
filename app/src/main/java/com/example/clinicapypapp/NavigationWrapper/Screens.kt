package com.example.clinicapypapp.NavigationWrapper

import kotlinx.serialization.Serializable

@Serializable
object LoginDest

@Serializable
data class MainDest(
    val idUsuario: Int
)

@Serializable
data class ServicesDest(
    val idUsuario: Int,
    val idSeccion: Int,
    val sectionName: String
)

@Serializable
data class UsuarioDest(
    val idUsuario: Int
)

@Serializable
data class CitaDest(val idUsuario: Int, val idSeccion: Int, val idServicio: Int, val sectionName: String)