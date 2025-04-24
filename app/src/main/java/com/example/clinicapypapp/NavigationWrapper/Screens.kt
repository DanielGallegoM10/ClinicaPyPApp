package com.example.clinicapypapp.NavigationWrapper

import kotlinx.serialization.Serializable

@Serializable
object LoginDest

@Serializable
object MainDest

@Serializable
data class ServicesDest(val sectionName: String)

@Serializable
data class CitaDest(val sectionName: String)