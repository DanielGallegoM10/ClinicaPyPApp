package com.example.clinicapypapp.NavigationWrapper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
object LoginScreen

@Serializable
object MainScreen

@Serializable
data class ServicesScreen(val sectionName: String)